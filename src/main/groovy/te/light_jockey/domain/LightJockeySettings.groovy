package te.light_jockey.domain

import groovy.transform.CompileStatic

@CompileStatic
class LightJockeySettings {
    String zoneName
    String sonosApiUrl
    String hueApiUrl
    String echoNestApiKey
    List<String> lightIds
}
