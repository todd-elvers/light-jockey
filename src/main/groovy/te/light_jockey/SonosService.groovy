package te.light_jockey

import wslite.rest.RESTClient

class SonosService {
    RESTClient sonosApiEndpoint

    SonosService(String sonosApiUrl) {
        sonosApiEndpoint = new RESTClient(sonosApiUrl)
    }


}
