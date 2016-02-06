package te.light_jockey.domain

import groovy.transform.CompileStatic

@CompileStatic
class LightJockeySettings {
    String zoneName
    List<String> lightIds
    String sonosApiUrl
    String hueApiUrl
    String echoNestApiKey
}
