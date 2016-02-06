package te.light_jockey.domain

import groovy.json.JsonSlurper
import spock.lang.Shared
import spock.lang.Specification
import te.light_jockey.domain.echo_nest.EchoNestSearch

class EchoNestSearchTest extends Specification {

    @Shared
    def jsonResponseStringWithResultsAndMetadata = """\
        {
            "response": {
                "status": {
                    "version": "4.2",
                    "code": 0,
                    "message": "Success"
                },
                "songs": [
                    {
                        "id": "SORVZNY1312FDFAF7B",
                        "artist_id": "AR4ZYGI1187B995AA2",
                        "artist_name": "The Postal Service",
                        "audio_summary": {
                              "key": 9,
                              "analysis_url": "http:\\/\\/echonest-analysis.s3.amazonaws.com\\/TR\\/qpl3IwtDzgf6bgkFHmwBEbI8sc7XONxVuTVQyw6ZtybOs_36eZ7VQw0w9yzLHWlgBPDOysg4rnuH0v-jQ%3D\\/3\\/full.json?AWSAccessKeyId=AKIAJRDFEY23UEVW42BQ&Expires=1454797898&Signature=hoObuXUGuRjOyISxkGkcBNMPu6A%3D",
                              "energy": 0.532549,
                              "liveness": 0.096416,
                              "tempo": 129.304,
                              "speechiness": 0.035785,
                              "acousticness": 0.007134,
                              "instrumentalness": 0.338333,
                              "mode": 0,
                              "time_signature": 4,
                              "duration": 348.98667,
                              "loudness": -8.353,
                              "audio_md5": "",
                              "valence": 0.62012,
                              "danceability": 0.781296
                        },
                        "title": "Such Great Heights (John Tejada Remix)"
                    },
                    {
                        "id": "SOYFIUN13DC54911A4",
                        "artist_id": "AR4ZYGI1187B995AA2",
                        "artist_name": "The Postal Service",
                        "audio_summary": {
                              "key": 9,
                              "analysis_url": "http:\\/\\/echonest-analysis.s3.amazonaws.com\\/TR\\/gkF43AFmXZIOyOtXSVv4VFKXujtzoP-P7VG9kB\\/3\\/full.json?AWSAccessKeyId=AKIAJRDFEY23UEVW42BQ&Expires=1454797898&Signature=U7PLuSQehSaDAlEtzg3nH6CpBS0%3D",
                              "energy": 0.482351,
                              "liveness": 0.107336,
                              "tempo": 124.985,
                              "speechiness": 0.035795,
                              "acousticness": 0.317682,
                              "instrumentalness": 0.16606,
                              "mode": 1,
                              "time_signature": 4,
                              "duration": 206.64272,
                              "loudness": -10.026,
                              "audio_md5": "b4ad9092de585cbf3b00a4dbce4c3170",
                              "valence": 0.193244,
                              "danceability": 0.570078
                        },
                        "title": "Be Still My Heart (Nobody Remix)"
                    }
                ]
            }
        }
    """

    @Shared
    def jsonResponseStringWithResultsButNoMetadata = """\
        {
            "response": {
                "status": {
                    "version": "4.2",
                    "code": 0,
                    "message": "Success"
                },
                "songs": [
                    {
                        "id": "SORVZNY1312FDFAF7B",
                        "artist_id": "AR4ZYGI1187B995AA2",
                        "artist_name": "The Postal Service",
                        "audio_summary": {},
                        "title": "Such Great Heights (John Tejada Remix)"
                    }
                ]
            }
        }
    """

    @Shared
    def jsonResponseStringWithoutResults = """\
        {
            "response": {
                "status": {
                    "version": "4.2",
                    "code": 0,
                    "message": "Success"
                },
                "songs": []
            }
        }
    """

    def "can parse a /api/v4/song/search JSON response from EchoNest containing results & metadata"() {
        when:
            def songMetadataSearchResults = new EchoNestSearch(new JsonSlurper().parseText(jsonResponseStringWithResultsAndMetadata))

        then:
            songMetadataSearchResults.echoNestResponseMessage == "Success"
            songMetadataSearchResults.echoNestResponseCode == 0
            songMetadataSearchResults.songs.size() == 2

        and: 'the first song result was parsed correctly'
            songMetadataSearchResults.songs[0].id == "SORVZNY1312FDFAF7B"
            songMetadataSearchResults.songs[0].artistId == "AR4ZYGI1187B995AA2"
            songMetadataSearchResults.songs[0].artistName == "The Postal Service"
            songMetadataSearchResults.songs[0].title == "Such Great Heights (John Tejada Remix)"
            songMetadataSearchResults.songs[0].metadata.key == 9
            songMetadataSearchResults.songs[0].metadata.analysisUrl == "http://echonest-analysis.s3.amazonaws.com/TR/qpl3IwtDzgf6bgkFHmwBEbI8sc7XONxVuTVQyw6ZtybOs_36eZ7VQw0w9yzLHWlgBPDOysg4rnuH0v-jQ%3D/3/full.json?AWSAccessKeyId=AKIAJRDFEY23UEVW42BQ&Expires=1454797898&Signature=hoObuXUGuRjOyISxkGkcBNMPu6A%3D"
            songMetadataSearchResults.songs[0].metadata.energy == 0.532549
            songMetadataSearchResults.songs[0].metadata.liveness == 0.096416
            songMetadataSearchResults.songs[0].metadata.tempo == 129.304
            songMetadataSearchResults.songs[0].metadata.speechiness == 0.035785
            songMetadataSearchResults.songs[0].metadata.acousticness == 0.007134
            songMetadataSearchResults.songs[0].metadata.instrumentalness == 0.338333
            songMetadataSearchResults.songs[0].metadata.mode == 0
            songMetadataSearchResults.songs[0].metadata.timeSignature == 4
            songMetadataSearchResults.songs[0].metadata.duration == 348.98667
            songMetadataSearchResults.songs[0].metadata.loudness == -8.353
            songMetadataSearchResults.songs[0].metadata.md5 == ""
            songMetadataSearchResults.songs[0].metadata.valence == 0.62012
            songMetadataSearchResults.songs[0].metadata.danceability == 0.781296

        and: 'the second song result was parsed correctly'
            songMetadataSearchResults.songs[1].id == "SOYFIUN13DC54911A4"
            songMetadataSearchResults.songs[1].artistId == "AR4ZYGI1187B995AA2"
            songMetadataSearchResults.songs[1].artistName == "The Postal Service"
            songMetadataSearchResults.songs[1].title == "Be Still My Heart (Nobody Remix)"
            songMetadataSearchResults.songs[1].metadata.key == 9
            songMetadataSearchResults.songs[1].metadata.analysisUrl == "http://echonest-analysis.s3.amazonaws.com/TR/gkF43AFmXZIOyOtXSVv4VFKXujtzoP-P7VG9kB/3/full.json?AWSAccessKeyId=AKIAJRDFEY23UEVW42BQ&Expires=1454797898&Signature=U7PLuSQehSaDAlEtzg3nH6CpBS0%3D"
            songMetadataSearchResults.songs[1].metadata.energy == 0.482351
            songMetadataSearchResults.songs[1].metadata.liveness == 0.107336
            songMetadataSearchResults.songs[1].metadata.tempo == 124.985
            songMetadataSearchResults.songs[1].metadata.speechiness == 0.035795
            songMetadataSearchResults.songs[1].metadata.acousticness == 0.317682
            songMetadataSearchResults.songs[1].metadata.instrumentalness == 0.16606
            songMetadataSearchResults.songs[1].metadata.mode == 1
            songMetadataSearchResults.songs[1].metadata.timeSignature == 4
            songMetadataSearchResults.songs[1].metadata.duration == 206.64272
            songMetadataSearchResults.songs[1].metadata.loudness == -10.026
            songMetadataSearchResults.songs[1].metadata.md5 == "b4ad9092de585cbf3b00a4dbce4c3170"
            songMetadataSearchResults.songs[1].metadata.valence == 0.193244
            songMetadataSearchResults.songs[1].metadata.danceability == 0.570078

    }

    def "can parse a /api/v4/song/search JSON response from EchoNest with results but no metadata"() {
        when:
            def songMetadataSearchResults = new EchoNestSearch(new JsonSlurper().parseText(jsonResponseStringWithResultsButNoMetadata))
        then:
            songMetadataSearchResults.echoNestResponseMessage == "Success"
            songMetadataSearchResults.echoNestResponseCode == 0
            songMetadataSearchResults.songs.size() == 1
            songMetadataSearchResults.songs[0].id == "SORVZNY1312FDFAF7B"
            songMetadataSearchResults.songs[0].artistId == "AR4ZYGI1187B995AA2"
            songMetadataSearchResults.songs[0].artistName == "The Postal Service"
            songMetadataSearchResults.songs[0].title == "Such Great Heights (John Tejada Remix)"
            songMetadataSearchResults.songs[0].metadata == null
    }

    def "can parse a /api/v4/song/search JSON response from EchoNest no results"() {
        when:
            def songMetadataSearchResults = new EchoNestSearch(new JsonSlurper().parseText(jsonResponseStringWithoutResults))
        then:
            songMetadataSearchResults.echoNestResponseMessage == "Success"
            songMetadataSearchResults.echoNestResponseCode == 0
            songMetadataSearchResults.songs.isEmpty()
    }
}
