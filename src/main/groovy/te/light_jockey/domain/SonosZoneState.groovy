package te.light_jockey.domain

class SonosZoneState {
    int volume
    int songNumber
    int elapsedTimeInSeconds
    boolean isMuted
    String elapsedTimeFormatted     // Format = xx:xx
    String zoneState
    String playerState
    SonosSong nextSong
    SonosSong currentSong
    SonosZonePlayMode zonePlayMode

    SonosZoneState(json) {
        volume = json.volume
        isMuted = json.mute
        songNumber = json.trackNo
        elapsedTimeInSeconds = json.elapsedTime
        elapsedTimeFormatted = json.elapsedTimeFormatted
        zoneState = json.zoneState
        playerState = json.playerState
        nextSong = new SonosSong(json.nextTrack)
        currentSong = new SonosSong(json.currentTrack)
        zonePlayMode = new SonosZonePlayMode(json.zonePlayMode)
    }
}

