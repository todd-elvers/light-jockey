package te.light_jockey.rest_services

import te.light_jockey.domain.sonos.SonosZoneStatus
import wslite.rest.RESTClient
import wslite.rest.Response


class SonosService {
    final RESTClient sonosApiEndpoint

    SonosService(String sonosApiUrl) {
        sonosApiEndpoint = new RESTClient(sonosApiUrl)
    }

    SonosZoneStatus getZoneStatus(String zoneName) {
        Response sonosZoneResponse = sonosApiEndpoint.get(path: "/$zoneName/state")
        return new SonosZoneStatus(sonosZoneResponse.json)
    }
}