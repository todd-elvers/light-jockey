package te.light_jockey

import groovy.util.logging.Slf4j
import te.light_jockey.domain.LightJockeySettings

import static te.light_jockey.misc.PropertiesFileReader.readAppProperty

@Slf4j
class LightJockey {
    private static final int STARTING_DELAY_IN_MS = 0
    private static final int INTERVAL_DELAY_IN_MS = 500

    final Timer scheduler = new Timer()
    final LightJockeySettings settings

    LightJockey(LightJockeySettings settings) {
        this.settings = settings
    }

    void start() {
        log.info("Welcome to LightJockey v${readAppProperty('version')}!")

        LightJockeyEngine lightJockeyEngine = new LightJockeyEngine(settings)
        scheduler.scheduleAtFixedRate(
                lightJockeyEngine,
                STARTING_DELAY_IN_MS,
                INTERVAL_DELAY_IN_MS
        )
    }

    void stop() {
        scheduler.cancel()
    }
}
