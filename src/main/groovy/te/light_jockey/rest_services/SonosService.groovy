package te.light_jockey.rest_services

import groovy.json.JsonSlurper
import te.light_jockey.domain.sonos.SonosZoneStatus
import wslite.rest.RESTClient
import wslite.rest.Response


class SonosService {
    final RESTClient sonosApiEndpoint

    SonosService(String sonosApiUrl) {
        sonosApiEndpoint = new RESTClient(sonosApiUrl)
    }

    SonosZoneStatus getZoneStatus(String zoneName) {
        Response sonosZoneResponse = sonosApiEndpoint.get(path: "/$zoneName/state".replaceAll("\\s", "%20"))
        def zone = new JsonSlurper().parse(sonosZoneResponse.data)
        return new SonosZoneStatus(zone)
    }
}
