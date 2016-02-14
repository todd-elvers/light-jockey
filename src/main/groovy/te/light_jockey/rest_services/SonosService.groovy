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
        String urlEncodedZoneName = URLEncoder.encode(zoneName, "UTF-8")
        Response sonosZoneResponse = sonosApiEndpoint.get(path: "/$urlEncodedZoneName/state")
        return new SonosZoneStatus(sonosZoneResponse.json)
    }

}
