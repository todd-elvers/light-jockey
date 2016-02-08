package te.light_jockey.rest_services

import groovy.util.logging.Slf4j
import te.light_jockey.domain.echo_nest.EchoNestSearch
import te.light_jockey.domain.echo_nest.EchoNestSong
import te.light_jockey.domain.hue.LightTransition
import wslite.rest.ContentType
import wslite.rest.RESTClient

import java.math.MathContext
import java.math.RoundingMode

@Slf4j
class PhillipsHueService {
    public static final Map TO_BRIGHT_WHITE = [on: true, sat: 50, bri: 200, hue: 10_000, transitionTime: 50]
    public static final MathContext TO_WHOLE_NUMBER = new MathContext(1, RoundingMode.HALF_UP)
    public static final int DANCEABILITY_DEFAULT = 50
    public static final int ENERGY_DEFAULT = 50
    public static final int TEMPO_DEFAULT = 100

    final RESTClient hueApiEndpoint

    PhillipsHueService(String hueBridgeUrl) {
        hueApiEndpoint = new RESTClient(hueBridgeUrl)
    }

    void triggerLightTransition(String lightId, Map payload) {
        hueApiEndpoint.put(path: "/lights/$lightId/state") {
            type ContentType.JSON
            charset "UTF-8"
            json payload
        }
    }

    Map buildTransitionPayload(LightTransition transition) {
        int randomIntBetween1and10 = randomIntBetween(1, 10)
        int thresholdValueForTurningOff = (transition.percentChanceToTurnOff * 10).round()
        boolean shouldRandomlyTurnOff = (randomIntBetween1and10 >= thresholdValueForTurningOff)

        Map payload = shouldRandomlyTurnOff ? [on: false] : [
                on            : true,
                hue           : randomIntBetween(0, 65000),
                bri           : randomIntBetween(transition.minBrightness, transition.maxBrightness),
                sat           : transition.saturation,
                transitionTime: transition.transitionDuration
        ]

        log.debug("Transition payload:")
        payload.each { key, value ->
            log.debug("{} = {}", key, value)
        }

        return payload
    }

    private static int randomIntBetween(int lowerBound, int upperBound) {
        // Since "nextInt(upperBound - lowerBound) + lowerBound" is inclusive on lowerBound
        // and exclusive on upperBound, we add one to upperBound to make it inclusive.
        int realUpperBound = upperBound + 1;
        return new Random().nextInt(realUpperBound - lowerBound) + lowerBound;
    }

    LightTransition buildLightTransition(EchoNestSearch search) {
        Integer danceability = DANCEABILITY_DEFAULT
        Integer energy = ENERGY_DEFAULT
        Integer tempo = TEMPO_DEFAULT

        if (search.hasResults() && search.songs.first().hasMetadata()) {
            EchoNestSong.Metadata metadata = search.songs.first().metadata
            danceability = (metadata.danceability * 100).round(TO_WHOLE_NUMBER).toInteger()
            energy = (metadata.energy * 100).round(TO_WHOLE_NUMBER).toInteger()
            tempo = (metadata.tempo * 100).round(TO_WHOLE_NUMBER).toInteger()
        }

        log.info "Danceability = ${danceability}% | Energy = ${energy}% | Tempo = ${tempo}bpm"

        return new LightTransition(danceability, energy, tempo)
    }
}
