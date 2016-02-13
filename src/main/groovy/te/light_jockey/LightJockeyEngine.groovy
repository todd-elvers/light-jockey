package te.light_jockey

import com.google.common.base.Stopwatch
import groovy.util.logging.Slf4j
import te.light_jockey.domain.LightJockeySettings
import te.light_jockey.domain.echo_nest.EchoNestSearch
import te.light_jockey.domain.hue.LightTransition
import te.light_jockey.domain.sonos.SonosZoneStatus
import te.light_jockey.rest_services.EchoNestService
import te.light_jockey.rest_services.PhillipsHueService
import te.light_jockey.rest_services.SonosService

import static java.util.concurrent.TimeUnit.SECONDS
import static te.light_jockey.rest_services.PhillipsHueService.TO_BRIGHT_WHITE

@Slf4j
class LightJockeyEngine extends TimerTask {

    final LightJockeySettings settings
    final SonosService sonosService
    final PhillipsHueService hueService
    final EchoNestService echoNestService

    Stopwatch timer = Stopwatch.createStarted()
    String currentSongTitle = 'No track'
    LightTransition lightTransition = new LightTransition()

    LightJockeyEngine(LightJockeySettings settings) {
        log.info(settings.toString())
        this.settings = settings
        this.sonosService = new SonosService(settings.sonosApiUrl)
        this.hueService = new PhillipsHueService(settings.hueApiUrl)
        this.echoNestService = new EchoNestService(settings.echoNestApiKey)
        registerShutdownHook()
    }

    @Override
    void run() {
        log.info("\rChecking Sonos player status...")
        SonosZoneStatus zoneStatus = sonosService.getZoneStatus(settings.zoneName)

        if (zoneStatus.isPausedOrStopped()) {
            log.info("\nSonos player has paused or stopped.")
            cancel()
            return
        }

        if (zoneStatus.isCurrentlyPlaying(currentSongTitle)) {
            log.info("\r${lightTransition.secondsBetweenTransitions - timer.elapsed(SECONDS)} seconds until next transition...")
            if (timer.elapsed(SECONDS) >= lightTransition.secondsBetweenTransitions) {
                transitionAllLights()
                resetTimer()
            }
        } else if (!zoneStatus.isCurrentlyPlaying(currentSongTitle)) {
            //TODO: Can I do away with this newline?
            log.info("\nNew song detected: '$zoneStatus.currentSong.title' by $zoneStatus.currentSong.artist")
            currentSongTitle = zoneStatus.currentSong.title

            EchoNestSearch search = echoNestService.search(zoneStatus.currentSong)
            lightTransition = hueService.buildLightTransition(search)

            transitionAllLights()
            resetTimer()
        }
    }

    @Override
    boolean cancel() {
        finalLightTransition()
        return super.cancel()
    }

    private void finalLightTransition() {
        log.info("\nTransitioning lights to white & exiting program.")
        hueService.triggerAllLightTransitions(settings.lightIds, [on: false])
        sleep(500)
        hueService.triggerAllLightTransitions(settings.lightIds, TO_BRIGHT_WHITE)
    }

    private void transitionAllLights() {
        log.info("\rTransitioning lights now.")
        settings.lightIds.each { lightId ->
            Map transitionPayload = hueService.buildTransitionPayload(lightTransition)
            hueService.triggerLightTransition(lightId, transitionPayload)
        }
    }

    private void resetTimer() {
        timer.reset().start()
    }

    private void registerShutdownHook() {
        addShutdownHook {
            finalLightTransition()
        }
    }
}
