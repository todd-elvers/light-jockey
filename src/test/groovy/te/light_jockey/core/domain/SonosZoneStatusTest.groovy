package te.light_jockey.core.domain

import groovy.json.JsonSlurper
import spock.lang.Shared
import spock.lang.Specification
import te.light_jockey.core.domain.sonos.SonosZoneStatus

class SonosZoneStatusTest extends Specification {

    @Shared
    String jsonResponseString = """\
        {
          "currentTrack":{
            "artist":"College",
            "title":"Teenage Color - Anoraak Remix",
            "album":"Nightdrive With You",
            "albumArtURI":"/getaa?s=1&u=x-sonos-spotify%3aspotify%253atrack%253a3DjBDQs8ebkxMBo2V8V3SH%3fsid%3d9%26flags%3d32",
            "duration":347,
            "uri":"x-sonos-spotify:spotify%3atrack%3a3DjBDQs8ebkxMBo2V8V3SH?sid=9&flags=32"
          },
          "nextTrack":{
            "artist":"Blacknuss",
            "title":"Thinking of You",
            "album":"3",
            "albumArtURI":"/getaa?s=1&u=x-sonos-spotify%3aspotify%253atrack%253a4U93TIa0X6jGQrTBGTkChH%3fsid%3d9%26flags%3d32",
            "duration":235,
            "uri":"x-sonos-spotify:spotify%3atrack%3a4U93TIa0X6jGQrTBGTkChH?sid=9&flags=32"
          },
          "volume":18,
          "mute":false,
          "trackNo":161,
          "elapsedTime":200,
          "elapsedTimeFormatted":"03:20",
          "zoneState":"PAUSED_PLAYBACK",
          "playerState":"PLAYING",
          "zonePlayMode":{
            "shuffle":false,
            "repeat":false,
            "crossfade":true
          }
        }
    """

    def "can parse a /<zone>/state JSON response from Sonos player"() {
        when:
            def sonosZoneState = new SonosZoneStatus(new JsonSlurper().parseText(jsonResponseString))

        then:
            sonosZoneState.currentSong.artist == "College"
            sonosZoneState.currentSong.title == "Teenage Color - Anoraak Remix"
            sonosZoneState.currentSong.album == "Nightdrive With You"
            sonosZoneState.currentSong.albumArtURI == "/getaa?s=1&u=x-sonos-spotify%3aspotify%253atrack%253a3DjBDQs8ebkxMBo2V8V3SH%3fsid%3d9%26flags%3d32"
            sonosZoneState.currentSong.durationInSeconds == 347
            sonosZoneState.currentSong.uri == "x-sonos-spotify:spotify%3atrack%3a3DjBDQs8ebkxMBo2V8V3SH?sid=9&flags=32"
            sonosZoneState.nextSong.artist == "Blacknuss"
            sonosZoneState.nextSong.title == "Thinking of You"
            sonosZoneState.nextSong.album == "3"
            sonosZoneState.nextSong.albumArtURI == "/getaa?s=1&u=x-sonos-spotify%3aspotify%253atrack%253a4U93TIa0X6jGQrTBGTkChH%3fsid%3d9%26flags%3d32"
            sonosZoneState.nextSong.durationInSeconds == 235
            sonosZoneState.nextSong.uri == "x-sonos-spotify:spotify%3atrack%3a4U93TIa0X6jGQrTBGTkChH?sid=9&flags=32"
            sonosZoneState.volume == 18
            !sonosZoneState.isMuted
            sonosZoneState.songNumber == 161
            sonosZoneState.elapsedTimeInSeconds == 200
            sonosZoneState.elapsedTimeFormatted == "03:20"
            sonosZoneState.zoneState == "PAUSED_PLAYBACK"
            sonosZoneState.playerState == "PLAYING"
            sonosZoneState.zonePlayMode.isCrossfadeOn
            !sonosZoneState.zonePlayMode.isShuffleOn
            !sonosZoneState.zonePlayMode.isRepeatOn
    }
}
