package te.light_jockey

import groovy.transform.CompileStatic
import te.light_jockey.domain.LightJockeySettings

@CompileStatic
class Startup {

    public static final LightJockeySettings settings = new LightJockeySettings(
            zoneName      : 'Living Room',
            echoNestApiKey: 'KRWFREWEPJ7APEI1T',
            sonosApiUrl   : 'http://localhost:5005/',
            hueApiUrl     : 'http://192.168.1.112/api/210b0eea1366f719644ef2e2307c1923',
            lightIds      : ['1','2','3']
    )

    public static void main(String... args) {
        new LightJockey(settings).start()
    }

}
