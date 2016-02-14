package te.light_jockey.rest_services

import groovy.util.logging.Slf4j
import te.light_jockey.domain.echo_nest.EchoNestSearch
import te.light_jockey.domain.sonos.SonosSong
import wslite.rest.RESTClient
import wslite.rest.Response

@Slf4j
class EchoNestService {
    final RESTClient echoNestApiEndpoint = new RESTClient('http://developer.echonest.com/')
    final String echoNestApiKey

    EchoNestService(String echoNestApiKey) {
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
                        bucket : 'audio_summary',   // Add the 'audio_summary' object to the response
                        results: 3                  // Limit the results to 3 songs
                ]
        )

        EchoNestSearch search = new EchoNestSearch(echoNestSearchResponse.json)
        if(search.hasResults()) {
            log.info "found song..."
            if(search.songs.first().hasMetadata()) {
                log.info "and metadata!!"
            } else {
                log.info "but no metadata."
            }
        } else {
            log.info "no results."
        }

        return search
    }
}
