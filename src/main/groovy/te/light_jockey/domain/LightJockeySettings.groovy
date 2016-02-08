package te.light_jockey.domain

import groovy.transform.CompileStatic

@CompileStatic
class LightJockeySettings {
    String zoneName
    String sonosApiUrl
    String hueApiUrl
    String echoNestApiKey
    List<String> lightIds

    @Override
    String toString() {
        """\
        LightJockey Settings:
            Sonos Endpoint   : $sonosApiUrl
            Hue Endpoint     : $hueApiUrl
            EchoNest API Key : $echoNestApiKey
            Sonos Zone Name  : $zoneName
            Light IDs        : $lightIds
        """.stripIndent()
    }
}
