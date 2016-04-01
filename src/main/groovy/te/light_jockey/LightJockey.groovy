package te.light_jockey

import groovy.util.logging.Slf4j

import static te.light_jockey.misc.PropertiesFileReader.readAppProperty

@Slf4j
class LightJockey {

    void start() {
        log.info("Welcome to LightJockey v${readAppProperty('version')}!\n")

    }
}
