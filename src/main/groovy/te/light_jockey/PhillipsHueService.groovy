package te.light_jockey

import wslite.rest.RESTClient

class PhillipsHueService {
    RESTClient hueApiEndpoint

    PhillipsHueService(String hueBridgeUrl) {
        hueApiEndpoint = new RESTClient(hueBridgeUrl)
    }


}
