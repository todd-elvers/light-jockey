package te.light_jockey.domain.sonos

class SonosZoneStatus {
    final int volume
    final int songNumber
    final int elapsedTimeInSeconds
    final boolean isMuted
    final String elapsedTimeFormatted     // Format = xx:xx
    final String zoneState
    final String playerState
    final SonosSong nextSong
    final SonosSong currentSong
    final SonosZonePlayMode zonePlayMode

    SonosZoneStatus(json) {
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

    boolean isPausedOrStopped() {
        return zoneState in ['PAUSED_PLAYBACK', 'STOPPED']
    }

    boolean isCurrentlyPlaying(String songTitle) {
        return songTitle.equalsIgnoreCase(currentSong.title)
    }
}

