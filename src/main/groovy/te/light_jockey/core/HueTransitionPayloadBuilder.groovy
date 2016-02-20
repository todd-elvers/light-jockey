package te.light_jockey.core

import groovy.util.logging.Slf4j
import te.light_jockey.core.domain.hue.HueTransitionProperties

@Slf4j
class HueTransitionPayloadBuilder {

    Map buildPayloadAsMap(HueTransitionProperties lightTransitionProps) {
        Map payload = [
                on            : true,
                hue           : randomIntBetween(0, 65535),
                bri           : randomIntBetween(lightTransitionProps.minBrightness, lightTransitionProps.maxBrightness),
                sat           : lightTransitionProps.saturation,
                transitiontime: lightTransitionProps.transitionDuration
        ]

        log.debug("Transition payload:")
        payload.each { key, value ->
            log.debug("{} = {}", key, value)
        }

        return payload
    }

    /**
     * Returns random int between two bounds inclusive.
     */
    public static int randomIntBetween(int lowerBound, int upperBound) {
        // Since "nextInt(upperBound - lowerBound) + lowerBound" is inclusive on lowerBound
        // and exclusive on upperBound, we add one to upperBound to make it inclusive.
        int realUpperBound = upperBound + 1;
        return new Random().nextInt(realUpperBound - lowerBound) + lowerBound;
    }

}
