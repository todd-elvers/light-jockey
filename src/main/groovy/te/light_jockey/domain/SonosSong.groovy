package te.light_jockey.domain

class SonosSong {
    String artist
    String title
    String album
    String albumArtURI
    String uri
    int durationInSeconds

    SonosSong(json) {
        artist = json.artist
        title = json.title
        album = json.album
        albumArtURI = json.albumArtURI
        durationInSeconds = json.duration
        uri = json.uri
    }
}
