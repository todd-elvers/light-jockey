package te.light_jockey.core.domain.sonos

class SonosSong {
    final String artist
    final String title
    final String album
    final String albumArtURI
    final String uri
    final Integer durationInSeconds

    SonosSong(json) {
        artist = json.artist
        title = json.title
        album = json.album
        albumArtURI = json.albumArtURI
        durationInSeconds = json.duration
        uri = json.uri
    }
}
