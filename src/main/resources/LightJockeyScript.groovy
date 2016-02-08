/*** This is after I just converted the python script to a Groovy script & was left around for reference ***/

//@GrabResolver(name = 'groovy-wslite', root = 'https://oss.sonatype.org/content/groups/public', m2Compatible = true)
//@Grab(group = 'com.github.groovy-wslite', module = 'groovy-wslite', version = '2.0.0-SNAPSHOT', changing = true)
//@Grab(group = 'com.google.guava', module = 'guava', version = '19.0')
import com.google.common.base.Stopwatch
import wslite.rest.ContentType
import wslite.rest.RESTClient

import javax.xml.ws.Response

import static java.util.concurrent.TimeUnit.SECONDS

/////////////////////////
///// Configuration /////
/////////////////////////

ZONE = 'Portable'
LIGHT_IDS = ['6', '7', '8', '9', '10', '11', '12', '13']
SONOS_INFO_URL = 'http://192.168.1.1:5005'
HUE_BRIDGE_URL = 'http://192.168.1.1/api/KEY'
ECHONEST_APIKEY = '0000000000000000'




////////////////////////////
////// Internal State //////
////////////////////////////

String currentSongName = 'No track'
Double percentChanceToTurnOff
Stopwatch timer = Stopwatch.createStarted()
int secondsBetweenTransitions = 10
int transitionDuration = 10
int minBrightness = 100
int maxBrightness = 100
int saturation = 100




///////////////////////
////// Main Loop //////
///////////////////////

RESTClient hueApiEndpoint = new RESTClient(HUE_BRIDGE_URL)
RESTClient sonosApiEndpoint = new RESTClient(SONOS_INFO_URL)
RESTClient songMetadataApiEndpoint = new RESTClient('http://developer.echonest.com/')
println("Welcome to LightJockey!  Starting up the funk...\n")
println("Making initial check to Sonos player.")
while (true) {
    // Get the current state of the Sonos player in a given zone
    Response sonosZoneResponse = sonosApiEndpoint.get(path: "/$ZONE/state")

    // Fade the lights back to white & stop program if the Sonos player is paused/stopped
    if (sonosZoneResponse.json.zoneState in ['PAUSED_PLAYBACK', 'STOPPED']) {
        println "Player has stopped.  Returning lights to white and exiting program."

        LIGHT_IDS.each { lightId ->
            hueApiEndpoint.put(path: "/lights/$lightId/state") {
                type ContentType.JSON
                charset "UTF-8"
                json([on: true, sat: 50, bri: 200, hue: 10_000, transitionTime: 50])
            }
        }
        break
    }


    String songName = sonosZoneResponse.json.currentTrack.title
    String songArtist = sonosZoneResponse.json.currentTrack.artist

    Integer danceability = 0
    Integer energy = 0
    Integer tempo = 0

    // Update the lights if a new song is playing, otherwise just print the status
    if (songName == currentSongName) {
        print "${secondsBetweenTransitions - timer.elapsed(SECONDS)} seconds until next transition...\r"
    } else {
        println "New song detected: $songName by $songArtist"

        print "\tLooking for song metadata online..."
        Response songLookupResponse = songMetadataApiEndpoint.get(
                path: '/api/v4/song/search',
                query: [
                        api_key: ECHONEST_APIKEY,
                        format : 'json',
                        artist : songArtist,
                        title  : songName
                ]
        )

        // Try and lookup the song's metadata to determine danceability, energy, and tempo
        boolean songMetadataFound = false
        if (songLookupResponse.json?.response?.songs) {
            print "song found..."

            String songId = songLookupResponse.json.response.songs[0].id
            Response songMetadataResponse = songMetadataApiEndpoint.get(
                    path: '/api/v4/song/profile',
                    query: [
                            id     : songId,
                            api_key: ECHONEST_APIKEY,
                            bucket : 'audio_summary'
                    ]
            )

            songMetadataFound = songMetadataResponse.json?.response?.songs?.getAt(0)?.audio_summary
            if (songMetadataFound) {
                println "metadata found!"
                def audioSummaryJson = songMetadataResponse.json.response.songs[0].audio_summary
                danceability = (audioSummaryJson.danceability * 100).toDouble().round().toInteger()
                energy = (audioSummaryJson.energy * 100).toDouble().round().toInteger()
                tempo = (audioSummaryJson.tempo * 100).toDouble().round().toInteger()
            } else {
                println "but no metadata."
            }
        } else {
            println "no results."
        }

        // If no metadata is found, fallback to static values for danceability, energy, and tempo
        if (!songMetadataFound) {
            println "\tUsing standard assumptions for this song."

            danceability = 50
            energy = 50
            tempo = 100
        }

        println "\tDanceability = ${danceability}% | Energy = ${energy}% | Tempo = ${tempo}bpm"

        currentSongName = songName

        // Higher tempo = faster transitions that are more frequent
        switch (tempo) {
            case (0..100):
                secondsBetweenTransitions = 10
                transitionDuration = 10
                break
            case (99..119):
                secondsBetweenTransitions = 5
                transitionDuration = 7
                break
            case (120..159):
                secondsBetweenTransitions = 2
                transitionDuration = 3
                break
            default:    // 160 bpm or greater
                secondsBetweenTransitions = 1
                transitionDuration = 0
        }

        // Higher energy = more saturation
        switch (energy) {
            case (0..19):
                saturation = 50
                break
            case (20..39):
                saturation = 100
                break
            case (40..59):
                saturation = 120
                break
            case (60..79):
                saturation = 170
                break
            case (80..99):
                saturation = 200
                break
            default:    // 100% or greater
                saturation = 225
        }

        // Higher danceability = brighter and more likely to switch on & off
        switch (danceability) {
            case (0..19):
                maxBrightness = 60
                minBrightness = 40
                percentChanceToTurnOff = 0
                break
            case (20..39):
                maxBrightness = 100
                minBrightness = 80
                percentChanceToTurnOff = 0
                break
            case (40..59):
                maxBrightness = 150
                minBrightness = 90
                percentChanceToTurnOff = 0.1
                break
            case (60..79):
                maxBrightness = 200
                minBrightness = 100
                percentChanceToTurnOff = 0.2
                break
            case (80..99):
                maxBrightness = 225
                minBrightness = 150
                percentChanceToTurnOff = 0.3
                break
            default:    // 100% or greater
                maxBrightness = 255
                minBrightness = 200
                percentChanceToTurnOff = 0.4
        }


        println "\tTransitioning lights now."
        LIGHT_IDS.each { lightId ->
            int randomIntBetween1and10 = randomIntBetween(1, 10)
            int thresholdValueForTurningOff = (percentChanceToTurnOff * 10).round()
            boolean shouldRandomlyTurnOff = (randomIntBetween1and10 >= thresholdValueForTurningOff)

            Map payload = shouldRandomlyTurnOff ? [on: false] : [
                    on            : true,
                    hue           : randomIntBetween(0, 65000),
                    bri           : randomIntBetween(minBrightness, maxBrightness),
                    sat           : saturation,
                    transitionTime: transitionDuration
            ]

            hueApiEndpoint.put(path: "/lights/$lightId/state") {
                type ContentType.JSON
                charset "UTF-8"
                json(payload)
            }

        }
        timer.reset().start()
    }

    // Update the lights if enough time has passed since the last update
    if (timer.elapsed(SECONDS) >= secondsBetweenTransitions) {
        println "Transitioning lights now."
        LIGHT_IDS.each { lightId ->
            int randomIntBetween1and10 = randomIntBetween(1, 10)
            int thresholdValueForTurningOff = (percentChanceToTurnOff * 10).round()
            boolean shouldRandomlyTurnOff = (randomIntBetween1and10 >= thresholdValueForTurningOff)

            Map payload = shouldRandomlyTurnOff ? [on: false] : [
                    on            : true,
                    hue           : randomIntBetween(0, 65000),
                    bri           : randomIntBetween(minBrightness, maxBrightness),
                    sat           : saturation,
                    transitionTime: transitionDuration
            ]

            hueApiEndpoint.put(path: "/lights/$lightId/state") {
                type ContentType.JSON
                charset "UTF-8"
                json(payload)
            }
        }

        timer.reset().start()
    }

    sleep(500)
}
println("LightJockey has stopped and the funk has ended.")



////////////////////////////
////// Helper Methods //////
////////////////////////////

int randomIntBetween(int lowerBound, int upperBound) {
    // Since "nextInt(upperBound - lowerBound) + lowerBound" is inclusive on lowerBound
    // and exclusive on upperBound, we add one to upperBound to make it inclusive.
    int realUpperBound = upperBound + 1;
    return new Random().nextInt(realUpperBound - lowerBound) + lowerBound;
}

