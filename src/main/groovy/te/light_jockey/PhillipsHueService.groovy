package te.light_jockey

import wslite.rest.ContentType
import wslite.rest.RESTClient

class PhillipsHueService {
    RESTClient hueApiEndpoint

    PhillipsHueService(String hueBridgeUrl) {
        hueApiEndpoint = new RESTClient(hueBridgeUrl)
    }


    void triggerLightTransition(String lightId, Map payload) {
        hueApiEndpoint.put(path: "/lights/$lightId/state") {
            type ContentType.JSON
            charset "UTF-8"
            json payload
        }
    }
}
