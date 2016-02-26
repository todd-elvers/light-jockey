package te.light_jockey.core.services

import groovy.util.logging.Slf4j
import te.light_jockey.core.domain.sonos.SonosZoneStatus
import wslite.rest.RESTClient
import wslite.rest.Response

@Slf4j
class SonosService extends ApiEndpointService {
    final RESTClient apiEndpoint

    SonosService(String sonosApiUrl) {
        apiEndpoint = new RESTClient(sonosApiUrl)
    }

    Optional<SonosZoneStatus> getZoneStatus(String zoneName) {
        SonosZoneStatus zoneStatus

        Optional<Response> responseOptional = super.get(path: "/${urlEncode(zoneName)}/state")
        if(responseOptional.isPresent()) {
            zoneStatus = new SonosZoneStatus(responseOptional.get().json)
        }

        return Optional.ofNullable(zoneStatus)
    }

    public static String urlEncode(String url) {
        URLEncoder.encode(url, "UTF-8").replaceAll("\\+", "%20")
    }
}
