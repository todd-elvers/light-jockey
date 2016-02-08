package te.light_jockey

import com.google.common.base.Stopwatch
import groovy.util.logging.Slf4j
import te.light_jockey.domain.LightJockeySettings
import te.light_jockey.domain.echo_nest.EchoNestSearch
import te.light_jockey.domain.hue.LightTransitionProperties
import te.light_jockey.domain.sonos.SonosZoneStatus
import te.light_jockey.rest_services.EchoNestService
import te.light_jockey.rest_services.PhillipsHueService
import te.light_jockey.rest_services.SonosService

import static PhillipsHueService.FADE_TO_WHITE_JSON_PAYLOAD
import static java.util.concurrent.TimeUnit.SECONDS

@Slf4j
class LightJockeyEngine {

    final LightJockeySettings settings
    final SonosService sonosService
    final PhillipsHueService hueService
    final EchoNestService echoNestService

    boolean shouldKeepRunning

    LightJockeyEngine(LightJockeySettings settings) {
        this.settings = settings
        this.sonosService = new SonosService(settings.sonosApiUrl)
        this.hueService = new PhillipsHueService(settings.hueApiUrl)
        this.echoNestService = new EchoNestService(settings.echoNestApiKey)
    }

    void start() {
        LightTransitionProperties lightTransitionProps = new LightTransitionProperties()
        Stopwatch timer = Stopwatch.createStarted()
        String currentSongTitle = 'No track'

        shouldKeepRunning = true

        while (shouldKeepRunning) {
            SonosZoneStatus zoneStatus = sonosService.getZoneStatus(settings.zoneName)

            if (zoneStatus.isPausedOrStopped()) {
                log.info("Sonos player has paused or stopped.")
                shouldKeepRunning = false
                continue
            }

            if (zoneStatus.isCurrentlyPlaying(currentSongTitle)) {
                log.info("\r${lightTransitionProps.secondsBetweenTransitions - timer.elapsed(SECONDS)} seconds until next transition...")
            } else {
                log.info("New song detected: $zoneStatus.currentSong.title by $zoneStatus.currentSong.artist")
                currentSongTitle = zoneStatus.currentSong.title

                EchoNestSearch search = echoNestService.search(zoneStatus.currentSong)
                lightTransitionProps = hueService.createLightTransitionProps(search)

                // TODO: In the future, maybe randomly use the same payload for all lights so they sync temporarily.  Might look cool.
                log.info "Transitioning lights now."
                settings.lightIds.each { lightId ->
                    hueService.triggerLightTransition(lightId, lightTransitionProps)
                }
                timer.reset().start()
            }

            if (timer.elapsed(SECONDS) >= lightTransitionProps.secondsBetweenTransitions) {
                log.info "Transitioning lights now."
                settings.lightIds.each { lightId ->
                    hueService.triggerLightTransition(lightId, lightTransitionProps)
                }
                timer.reset().start()
            }

            sleep(500)
        }

        log.info("Transitioning lights to white & exiting program.")
        settings.lightIds.each { lightId ->
            hueService.triggerLightTransition(lightId, FADE_TO_WHITE_JSON_PAYLOAD)
        }
    }

    void stop() {
        shouldKeepRunning = true
    }
}
