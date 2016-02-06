package te.light_jockey

import com.google.common.base.Stopwatch
import groovy.util.logging.Slf4j
import te.light_jockey.domain.LightJockeySettings
import te.light_jockey.domain.echo_nest.EchoNestSearch
import te.light_jockey.domain.echo_nest.EchoNestSong

import te.light_jockey.domain.sonos.SonosZoneStatus

import static java.util.concurrent.TimeUnit.SECONDS

@Slf4j
class LightJockeyEngine {
    static void main(String... args) {
        String echoNestApiKey = 'CHDHSEXYTNSXNZNBB'
        String sonosInfoUrl = 'http://192.168.1.1:5005'
        String hueBridgeUrl = 'http://192.168.1.1/api/KEY'
        new LightJockeyEngine(null)
    }

    String currentSongTitle = 'No track'
    int secondsBetweenTransitions = 10
    Double percentChanceToTurnOff
    int transitionDuration = 10
    int minBrightness = 100
    int maxBrightness = 100
    int saturation = 100

    Stopwatch timer = Stopwatch.createUnstarted()

    private static final int DANCEABILITY_DEFAULT = 50
    private static final int ENERGY_DEFAULT = 50
    private static final int TEMPO_DEFAULT = 100
    private static
    final Map FADE_TO_WHITE = [on: true, sat: 50, bri: 200, hue: 10_000, transitionTime: 50]

    LightJockeySettings settings
    SonosService sonosService
    PhillipsHueService hueService
    EchoNestMetadataService echoNestMetadataService

    LightJockeyEngine(LightJockeySettings settings) {
        this.settings = settings
        this.hueService = new PhillipsHueService(settings.hueApiUrl)
        this.sonosService = new SonosService(settings.sonosApiUrl)
        this.echoNestMetadataService = new EchoNestMetadataService(settings.echoNestApiKey)
    }

    void start() {
        timer.start()
        while (true) {
            SonosZoneStatus zoneStatus = sonosService.getZoneStatus(settings.zoneName)

            if (zoneStatus.isPausedOrStopped()) {
                log.info("Player has stopped.  Returning lights to white & exiting program.")
                settings.lightIds.each { lightId ->
                    hueService.triggerLightTransition(lightId, FADE_TO_WHITE)
                }

                break
            }

            if (zoneStatus.currentSong.title == currentSongTitle) {
                log.info("\r${secondsBetweenTransitions - timer.elapsed(SECONDS)} seconds until next transition...")
            } else {
                log.info("New song detected: $zoneStatus.currentSong.title by $zoneStatus.currentSong.artist")

                EchoNestSearch search = echoNestMetadataService.search(zoneStatus.currentSong)

                Integer danceability = DANCEABILITY_DEFAULT
                Integer energy = ENERGY_DEFAULT
                Integer tempo = TEMPO_DEFAULT

                if (search.hasResults() && search.songs.first().hasMetadata()) {
                    EchoNestSong.Metadata metadata = search.songs.first().metadata
                    danceability = (metadata.danceability * 100).toDouble().round().toInteger()
                    energy = (metadata.energy * 100).toDouble().round().toInteger()
                    tempo = (metadata.tempo * 100).toDouble().round().toInteger()
                }

                log.info "Danceability = ${danceability}% | Energy = ${energy}% | Tempo = ${tempo}bpm"

                currentSongTitle = zoneStatus.currentSong.title
                updateLightVariables(danceability, energy, tempo)

                log.info "Transitioning lights now."
                settings.lightIds.each { lightId ->
                    hueService.triggerLightTransition(lightId, buildJsonPayload())
                }
                timer.reset().start()
            }

            if (timer.elapsed(SECONDS) >= secondsBetweenTransitions) {
                log.info "Transitioning lights now."
                settings.lightIds.each { lightId ->
                    hueService.triggerLightTransition(lightId, buildJsonPayload())
                }
                timer.reset().start()
            }

            sleep(500)
        }
    }

    private void updateLightVariables(Integer danceability, Integer energy, Integer tempo) {
        // Higher tempo = faster transitions that are more frequent
        switch (tempo) {
            case (0..100):
                secondsBetweenTransitions = 10
                transitionDuration = 10
                break
            case (99..119):
                secondsBetweenTransitions = 5
                transitionDuration = 7
                break
            case (120..159):
                secondsBetweenTransitions = 2
                transitionDuration = 3
                break
            default:    // 160 bpm or greater
                secondsBetweenTransitions = 1
                transitionDuration = 0
        }

        // Higher energy = more saturation
        switch (energy) {
            case (0..19):
                saturation = 50
                break
            case (20..39):
                saturation = 100
                break
            case (40..59):
                saturation = 120
                break
            case (60..79):
                saturation = 170
                break
            case (80..99):
                saturation = 200
                break
            default:    // 100% or greater
                saturation = 225
        }

        // Higher danceability = brighter and more likely to switch on & off
        switch (danceability) {
            case (0..19):
                maxBrightness = 60
                minBrightness = 40
                percentChanceToTurnOff = 0
                break
            case (20..39):
                maxBrightness = 100
                minBrightness = 80
                percentChanceToTurnOff = 0
                break
            case (40..59):
                maxBrightness = 150
                minBrightness = 90
                percentChanceToTurnOff = 0.1
                break
            case (60..79):
                maxBrightness = 200
                minBrightness = 100
                percentChanceToTurnOff = 0.2
                break
            case (80..99):
                maxBrightness = 225
                minBrightness = 150
                percentChanceToTurnOff = 0.3
                break
            default:    // 100% or greater
                maxBrightness = 255
                minBrightness = 200
                percentChanceToTurnOff = 0.4
        }
    }

    private Map buildJsonPayload() {
        int randomIntBetween1and10 = randomIntBetween(1, 10)
        int thresholdValueForTurningOff = (percentChanceToTurnOff * 10).round()
        boolean shouldRandomlyTurnOff = (randomIntBetween1and10 >= thresholdValueForTurningOff)

        Map payload = shouldRandomlyTurnOff ? [on: false] : [
                on            : true,
                hue           : randomIntBetween(0, 65000),
                bri           : randomIntBetween(minBrightness, maxBrightness),
                sat           : saturation,
                transitionTime: transitionDuration
        ]

        return payload
    }

    private static int randomIntBetween(int lowerBound, int upperBound) {
        // Since "nextInt(upperBound - lowerBound) + lowerBound" is inclusive on lowerBound
        // and exclusive on upperBound, we add one to upperBound to make it inclusive.
        int realUpperBound = upperBound + 1;
        return new Random().nextInt(realUpperBound - lowerBound) + lowerBound;
    }
}
