package te.light_jockey.domain.hue

import groovy.transform.ToString
import groovy.util.logging.Slf4j

@Slf4j
@ToString(includeNames = true)
class LightTransition {
    Double percentChanceToTurnOff
    int secondsBetweenTransitions = 10
    int transitionDuration = 10
    int minBrightness = 100
    int maxBrightness = 100
    int saturation = 100

    LightTransition(){}

    LightTransition(Integer danceability, Integer energy, Integer tempo) {
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
                maxBrightness = 80
                minBrightness = 60
                percentChanceToTurnOff = 0
                break
            case (40..59):
                maxBrightness = 100
                minBrightness = 80
                percentChanceToTurnOff = 1
                break
            case (60..79):
                maxBrightness = 115
                minBrightness = 90
                percentChanceToTurnOff = 2
                break
            case (80..99):
                maxBrightness = 130
                minBrightness = 110
                percentChanceToTurnOff = 3
                break
            default:    // >= 100
                maxBrightness = 140
                minBrightness = 120
                percentChanceToTurnOff = 5
        }

        log.debug(this.toString())
    }
}
