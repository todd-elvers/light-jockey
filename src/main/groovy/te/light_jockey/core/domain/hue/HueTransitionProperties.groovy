package te.light_jockey.core.domain.hue

import groovy.util.logging.Slf4j
import te.light_jockey.core.domain.echo_nest.EchoNestSearch
import te.light_jockey.core.domain.echo_nest.EchoNestSong

@Slf4j
class HueTransitionProperties {
    public static final int DANCEABILITY_DEFAULT = 50
    public static final int ENERGY_DEFAULT = 50
    public static final int TEMPO_DEFAULT = 100

    int secondsBetweenTransitions = 10

    int transitionDuration = 50 // In centi-seconds (1 sec = 100 cs = 1000 ms)
    int saturation = 100        // Valid values: 0-254
    int minBrightness = 60      // Valid values: 0-254
    int maxBrightness = 254     // Valid values: 0-254

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

        log.info("Danceability = ${danceability}% | Energy = ${energy}% | Tempo = ${tempo}bpm")

        updatePropertiesWithTempo(tempo)
        updatePropertiesWithEnergy(energy)
        updatePropertiesWithDanceability(danceability)

        log.debug("{}", this.toString())
    }



    /**
     * Higher tempo = faster transitions that are more frequent
     */
    private void updatePropertiesWithTempo(int tempo) {
        switch (tempo) {
            case (0..89):
                secondsBetweenTransitions = 10
                transitionDuration = 25
                break
            case (90..119):
                secondsBetweenTransitions = 5
                transitionDuration = 15
                break
            case (120..159):
                secondsBetweenTransitions = 2
                transitionDuration = 0
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
                saturation = 250
        }
    }

    /**
     * Higher danceability = brighter and more likely to switch on & off
     */
    private void updatePropertiesWithDanceability(int danceability) {
        switch (danceability) {
            case (0..19):
                minBrightness = 40
                maxBrightness = 60
                break
            case (20..39):
                minBrightness = 60
                maxBrightness = 80
                break
            case (40..59):
                minBrightness = 80
                maxBrightness = 100
                break
            case (60..79):
                minBrightness = 90
                maxBrightness = 115
                break
            case (80..99):
                minBrightness = 110
                maxBrightness = 130
                break
            default:    // >= 100
                minBrightness = 120
                maxBrightness = 140
        }
    }

    @Override
    String toString() {
        return "Hue transition properties:\n" +
                getProperties()
                .findAll { it.key != 'class' }
                .collect { "\t$it.key = $it.value"}
    }
}
