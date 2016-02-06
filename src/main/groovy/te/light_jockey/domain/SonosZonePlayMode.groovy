package te.light_jockey.domain

class SonosZonePlayMode {
    boolean isShuffleOn
    boolean isRepeatOn
    boolean isCrossfadeOn

    SonosZonePlayMode(json) {
        isShuffleOn = json.shuffle
        isRepeatOn = json.repeat
        isCrossfadeOn = json.crossfade
    }
}
