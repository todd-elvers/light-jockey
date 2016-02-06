package te.light_jockey.domain.sonos

class SonosSong {
    final String artist
    final String title
    final String album
    final String albumArtURI
    final String uri
    final int durationInSeconds

    SonosSong(json) {
        artist = json.artist
        title = json.title
        album = json.album
        albumArtURI = json.albumArtURI
        durationInSeconds = json.duration
        uri = json.uri
    }
}
