package te.light_jockey

import te.light_jockey.domain.LightJockeySettings
import te.light_jockey.misc.PropertiesFileReader

class Startup {

    public static void main(String... args) {
        //TODO: Add validation to this process eventually
        Properties props = PropertiesFileReader.readFile('light-jockey-settings.properties')

        LightJockeySettings settings = new LightJockeySettings(
                zoneName      : props.zoneName,
                echoNestApiKey: props.echoNestApiKey,
                sonosApiUrl   : props.sonosApiUrl,
                hueApiUrl     : props.hueApiUrl,
                lightIds      : props.lightIds.toString().split(',')*.trim()
        )


        new LightJockey(settings).start()
    }

}
