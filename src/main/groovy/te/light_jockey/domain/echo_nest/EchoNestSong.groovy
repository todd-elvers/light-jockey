package te.light_jockey.domain.echo_nest

class EchoNestSong {
    final String id
    final String artistId
    final String artistName
    final String title
    final Metadata metadata

    EchoNestSong(json) {
        id = json.id
        artistId = json.artist_id
        artistName = json.artist_name
        title = json.title
        if(json.audio_summary) {
            metadata = new Metadata(json.audio_summary)
        }
    }

    boolean hasMetadata() {
        return metadata
    }

    static class Metadata {
        final int key
        final String analysisUrl
        final BigDecimal energy
        final BigDecimal liveness
        final BigDecimal tempo
        final BigDecimal speechiness
        final BigDecimal acousticness
        final BigDecimal instrumentalness
        final int mode
        final int timeSignature
        final BigDecimal duration
        final BigDecimal loudness
        final String md5
        final BigDecimal valence
        final BigDecimal danceability

        Metadata(json) {
            key = json.key
            analysisUrl = json.analysis_url
            energy = json.energy
            liveness = json.liveness
            tempo = json.tempo
            speechiness = json.speechiness
            acousticness = json.acousticness
            instrumentalness = json.instrumentalness
            mode = json.mode
            timeSignature = json.time_signature
            duration = json.duration
            loudness = json.loudness
            md5 = json.audio_md5
            valence = json.valence
            danceability = json.danceability
        }
    }
}
