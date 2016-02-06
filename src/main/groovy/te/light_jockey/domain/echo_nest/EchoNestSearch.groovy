package te.light_jockey.domain.echo_nest

//TODO: Either keep with the 'SongMetadata' strategy or switch to an explicit 'EchoNest' naming scheme
class EchoNestSearch {

    final int echoNestResponseCode
    final String echoNestResponseMessage
    final List<EchoNestSong> songs

    EchoNestSearch(json) {
        echoNestResponseCode = json.response.status.code
        echoNestResponseMessage = json.response.status.message
        songs = json.response.songs.collect { new EchoNestSong(it) }
    }

    boolean hasResults() {
        return songs
    }
}
