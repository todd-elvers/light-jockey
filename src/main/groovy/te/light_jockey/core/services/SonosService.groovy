package te.light_jockey.core.services

import te.light_jockey.core.domain.sonos.SonosZoneStatus
import wslite.rest.RESTClient
import wslite.rest.Response

class SonosService {
    final RESTClient sonosApiEndpoint

    SonosService(String sonosApiUrl) {
        sonosApiEndpoint = new RESTClient(sonosApiUrl)
    }

    SonosZoneStatus getZoneStatus(String zoneName) {
        Response sonosZoneResponse = sonosApiEndpoint.get(path: "/${urlEncode(zoneName)}/state")
        return new SonosZoneStatus(sonosZoneResponse.json)
    }

    public static String urlEncode(String url) {
        URLEncoder.encode(url, "UTF-8").replaceAll("\\+", "%20")
    }
}
