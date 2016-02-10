package te.light_jockey

import te.light_jockey.domain.LightJockeySettings
import te.light_jockey.misc.PropertiesFileReader

class Startup {

    public static void main(String... args) {
        //TODO: Add validation to this process eventually
        Properties propFile = PropertiesFileReader.readFile('light-jockey-settings.properties')

        LightJockeySettings settings = new LightJockeySettings(
                zoneName      : propFile.zoneName,
                echoNestApiKey: propFile.echoNestApiKey,
                sonosApiUrl   : propFile.sonosApiUrl,
                hueApiUrl     : propFile.hueApiUrl,
                lightIds      : propFile.lightIds.toString().split(',')*.trim()
        )


        new LightJockey(settings).start()
    }

}
