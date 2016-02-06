package te.light_jockey

import wslite.rest.RESTClient

class SongMetadataService {
    RESTClient songMetadataApiEndpoint = new RESTClient('http://developer.echonest.com/')
}
