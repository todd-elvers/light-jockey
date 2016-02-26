package te.light_jockey.core

import com.google.common.base.Stopwatch
import groovy.util.logging.Slf4j
import te.light_jockey.core.domain.LightJockeySettings
import te.light_jockey.core.domain.echo_nest.EchoNestSearch
import te.light_jockey.core.domain.hue.HueTransitionProperties
import te.light_jockey.core.domain.sonos.SonosZoneStatus
import te.light_jockey.core.services.EchoNestService
import te.light_jockey.core.services.HueService
import te.light_jockey.core.services.SonosService

import static java.util.concurrent.TimeUnit.SECONDS

@Slf4j
class LightJockeyEngine extends TimerTask {

    final LightJockeySettings settings
    final SonosService sonosService
    final HueService hueService
    final EchoNestService echoNestService

    Stopwatch transitionTimer = Stopwatch.createStarted()
    String currentSongTitle = 'No track'
    HueTransitionProperties hueTransitionProps = new HueTransitionProperties()

    LightJockeyEngine(LightJockeySettings settings) {
        log.info(settings.toString())
        this.settings = settings
        this.sonosService = new SonosService(settings.sonosApiUrl)
        this.hueService = new HueService(settings.hueApiUrl)
        this.echoNestService = new EchoNestService(settings.echoNestApiKey)
        registerShutdownHook()
    }

    @Override
    void run() {
        log.info("\rChecking Sonos player status...")
        Optional<SonosZoneStatus> response = sonosService.getZoneStatus(settings.zoneName)

        if(!response.isPresent()) return

        SonosZoneStatus zoneStatus = response.get()

        if (zoneStatus.isPausedOrStopped()) {
            log.info("Sonos player has paused or stopped.")
            cancel()
        } else if (zoneStatus.isNotCurrentlyPlaying(currentSongTitle)) {
            log.info("New song detected: '$zoneStatus.currentSong.title' by $zoneStatus.currentSong.artist")
            updateCurrentSongTitle(zoneStatus)
            Optional<EchoNestSearch> search = echoNestService.search(zoneStatus.currentSong)
            if(search.isPresent()) {
                hueTransitionProps.update(search.get())
            }
            performLightTransitionThenResetTimer()
        } else {
            log.info("\r${calcSecondsToNextTransition()} seconds until next transition...")
            if (enoughTimeHasPassedSinceLastTransition()) {
                performLightTransitionThenResetTimer()
            }
        }
    }

    @Override
    boolean cancel() {
        hueService.finalTransition(settings.lightIds)
        return super.cancel()
    }

    private Integer calcSecondsToNextTransition() {
        hueTransitionProps.secondsBetweenTransitions - transitionTimer.elapsed(SECONDS)
    }

    private boolean enoughTimeHasPassedSinceLastTransition() {
        transitionTimer.elapsed(SECONDS) >= hueTransitionProps.secondsBetweenTransitions
    }

    private void performLightTransitionThenResetTimer() {
        hueService.transitionAllLightsWithDiffPayloads(settings.lightIds, hueTransitionProps)
        transitionTimer.reset().start()
    }

    private void updateCurrentSongTitle(SonosZoneStatus zoneStatus) {
        currentSongTitle = zoneStatus.currentSong.title
    }

    //TODO: Does this trigger twice with if LightJockey.stop() is called?
    private void registerShutdownHook() {
        addShutdownHook {
            hueService.finalTransition(settings.lightIds)
        }
    }
}