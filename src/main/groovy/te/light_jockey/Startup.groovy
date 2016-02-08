package te.light_jockey

import groovy.transform.CompileStatic
import te.light_jockey.domain.LightJockeySettings

@CompileStatic
class Startup {

    public static final LightJockeySettings settings = new LightJockeySettings(
            zoneName      : 'Portable',
            echoNestApiKey: 'CHDHSEXYTNSXNZNBB',
            sonosApiUrl   : 'http://192.168.1.1:5005',
            hueApiUrl     : 'http://192.168.1.1/api/KEY',
            lightIds      : ['1', '2', '3', '4']
    )

    public static void main(String... args) {
        new LightJockeyEngine(settings).start()
    }

}
