package te.light_jockey.core.domain.sonos

class SonosZonePlayMode {
    final boolean isShuffleOn
    final boolean isRepeatOn
    final boolean isCrossfadeOn

    SonosZonePlayMode(json) {
        isShuffleOn = json.shuffle
        isRepeatOn = json.repeat
        isCrossfadeOn = json.crossfade
    }
}
