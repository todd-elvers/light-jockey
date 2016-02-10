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
    }

    @Override
    void run() {
        log.info("\rChecking Sonos player status...")
        SonosZoneStatus zoneStatus = sonosService.getZoneStatus(settings.zoneName)

        if (zoneStatus.isPausedOrStopped()) {
            log.info("Sonos player has paused or stopped.")
            cancel()
            return
        }

        if (zoneStatus.isCurrentlyPlaying(currentSongTitle)) {
            log.info("\r${lightTransition.secondsBetweenTransitions - timer.elapsed(SECONDS)} seconds until next transition...")
            if (timer.elapsed(SECONDS) >= lightTransition.secondsBetweenTransitions) {
                transitionAllLights()
                resetTimer()
            }
        } else if(!zoneStatus.isCurrentlyPlaying(currentSongTitle)) {
            log.info("New song detected: '$zoneStatus.currentSong.title' by $zoneStatus.currentSong.artist")
            currentSongTitle = zoneStatus.currentSong.title

            EchoNestSearch search = echoNestService.search(zoneStatus.currentSong)
            lightTransition = hueService.buildLightTransition(search)

            transitionAllLights()
            resetTimer()
        }
    }

    @Override
    boolean cancel() {
        log.info("Transitioning lights to white & exiting program.")
        settings.lightIds.each { lightId ->
            hueService.triggerLightTransition(lightId, TO_BRIGHT_WHITE)
        }
        return super.cancel()
    }

    // TODO: In the future, maybe randomly use the same payload for all lights so they sync temporarily.  Might look cool.
    private void transitionAllLights() {
        Map transitionPayload = hueService.buildTransitionPayload(lightTransition)
        log.info "\rTransitioning lights now."
        settings.lightIds.each { lightId ->
            hueService.triggerLightTransition(lightId, transitionPayload)
        }
    }

    private void resetTimer() {
        timer.reset().start()
    }
}
