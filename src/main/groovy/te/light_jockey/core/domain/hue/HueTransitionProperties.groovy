package te.light_jockey.core.domain.hue

import groovy.util.logging.Slf4j
import te.light_jockey.core.domain.echo_nest.EchoNestSearch
import te.light_jockey.core.domain.echo_nest.EchoNestSong

@Slf4j
class HueTransitionProperties {
    public static final int DANCEABILITY_DEFAULT = 50
    public static final int ENERGY_DEFAULT = 50
    public static final int TEMPO_DEFAULT = 100

    Double percentChanceToTurnOff
    int secondsBetweenTransitions = 10
    int transitionDuration = 10
    int minBrightness = 100
    int maxBrightness = 100
    int saturation = 100

    void update(EchoNestSearch search) {
        Integer danceability = DANCEABILITY_DEFAULT
        Integer energy = ENERGY_DEFAULT
        Integer tempo = TEMPO_DEFAULT

        if (search.hasResults() && search.songs.first().hasMetadata()) {
            EchoNestSong.Metadata metadata = search.songs.first().metadata
            danceability = (metadata.danceability * 100).toInteger()
            energy = (metadata.energy * 100).toInteger()
            tempo = (metadata.tempo).toInteger()
        }

        log.info "Danceability = ${danceability}% | Energy = ${energy}% | Tempo = ${tempo}bpm"

        updatePropertiesWithTempo(tempo)
        updatePropertiesWithEnergy(energy)
        updatePropertiesWithDanceability(danceability)

        log.debug "Hue transition properties:\n{}", this.toString()
    }

    /**
     * Higher tempo = faster transitions that are more frequent
     */
    private void updatePropertiesWithTempo(int tempo) {
        switch (tempo) {
            case (0..99):
                secondsBetweenTransitions = 10
                transitionDuration = 10
                break
            case (100..119):
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
    }

    /**
     * Higher energy = higher saturation
     */
    private void updatePropertiesWithEnergy(int energy) {
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
    }

    /**
     * Higher danceability = brighter and more likely to switch on & off
     */
    private void updatePropertiesWithDanceability(int danceability) {
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
                percentChanceToTurnOff = 1
                break
            case (80..99):
                maxBrightness = 130
                minBrightness = 110
                percentChanceToTurnOff = 1
                break
            default:    // >= 100
                maxBrightness = 140
                minBrightness = 120
                percentChanceToTurnOff = 3
        }
    }

    @Override
    String toString() {
        return this.properties
                .findAll { it.key != 'class' }
                .collect { "\t$it.key = $it.value"}
    }
}
