package te.light_jockey

import groovy.util.logging.Slf4j
import te.light_jockey.domain.echo_nest.EchoNestSearch
import te.light_jockey.domain.sonos.SonosSong
import wslite.rest.RESTClient
import wslite.rest.Response

@Slf4j
class EchoNestMetadataService {
    final RESTClient echoNestApiEndpoint = new RESTClient('http://developer.echonest.com/')
    final String echoNestApiKey

    EchoNestMetadataService(String echoNestApiKey) {
        this.echoNestApiKey = echoNestApiKey
    }

    EchoNestSearch search(SonosSong song) {
        log.info("Looking for metadata online...")
        Response echoNestSearchResponse = echoNestApiEndpoint.get(
                path: '/api/v4/song/search',
                query: [
                        api_key: echoNestApiKey,
                        format : 'json',
                        artist : song.artist,
                        title  : song.title,
                        bucket : 'audio_summary',
                        results: 3
                ]
        )

        EchoNestSearch search = new EchoNestSearch(echoNestSearchResponse.json)
        if(search.hasResults()) {
            log.info "found song..."
            if(search.songs.first().hasMetadata()) {
                log.info "metadata found!"
            } else {
                log.info "but no metadata."
            }
        } else {
            log.info "no results."
        }

        return search
    }
}
