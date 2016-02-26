package te.light_jockey.core.services

import groovy.util.logging.Slf4j
import te.light_jockey.core.domain.echo_nest.EchoNestSearch
import te.light_jockey.core.domain.sonos.SonosSong
import wslite.rest.RESTClient
import wslite.rest.Response

@Slf4j
class EchoNestService extends ApiEndpointService {
    final RESTClient apiEndpoint = new RESTClient('http://developer.echonest.com/')
    final String echoNestApiKey

    EchoNestService(String echoNestApiKey) {
        this.echoNestApiKey = echoNestApiKey
    }

    Optional<EchoNestSearch> search(SonosSong song) {
        EchoNestSearch search

        log.info("Looking for metadata online...")
        Optional<Response> response = super.get(
                path : '/api/v4/song/search',
                query: [
                        api_key: echoNestApiKey,
                        format : 'json',
                        artist : song.artist,
                        title  : song.title,
                        bucket : 'audio_summary',   // Add the 'audio_summary' object to the response
                        results: 3                  // Limit the results to 3 songs
                ]
        )
        if (response.isPresent()) {
            search = new EchoNestSearch(response.get().json)
        }

        foundSearchResultsWithMetadata(search) ? log.info("success!") : log.info("no results.")
        return Optional.ofNullable(search)
    }

    private static boolean foundSearchResultsWithMetadata(EchoNestSearch search) {
        search && search.hasResults() && search.songs.first().hasMetadata()
    }
}
