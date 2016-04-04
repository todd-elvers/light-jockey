package te.light_jockey.core

import com.philips.lighting.hue.sdk.PHHueSDK
import com.philips.lighting.model.PHBridgeResourcesCache
import com.philips.lighting.model.PHLight
import com.philips.lighting.model.PHLightState
import groovy.util.logging.Slf4j
import te.light_jockey.core.audio_processing.SoundPressureLevelDetector
import te.light_jockey.core.audio_processing.tarsos_dsp.AudioDispatcher
import te.light_jockey.core.audio_processing.tarsos_dsp.AudioDispatcherFactory
import te.light_jockey.core.audio_processing.tarsos_dsp.AudioEvent
import te.light_jockey.core.audio_processing.tarsos_dsp.AudioProcessor

import static te.light_jockey.core.io.PropertiesFileReader.readAppProperty

@Slf4j
class LightJockey implements AudioProcessor {
    private static final int SAMPLE_RATE = 44100
    private static final int AUDIO_BUFFER_SIZE = 512
    private static final int BUFFER_OVERLAP = 0

    double threshold = -47
    PHHueSDK hueSDK = PHHueSDK.getInstance()
    SoundPressureLevelDetector splDetector = new SoundPressureLevelDetector()


    void start() {
        log.info("Welcome to LightJockey v${readAppProperty('version')}!\n")

        // Create an audio dispatcher to dispatch data from the microphone to audio processor(s)
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLE_RATE, AUDIO_BUFFER_SIZE, BUFFER_OVERLAP)
        dispatcher.addAudioProcessor(splDetector)   // Add our sound pressure level detector to translate the raw microphone data to dB SPL
        dispatcher.addAudioProcessor(this)          // Add our LightJockey processor so we can read the dB SPL value from the SPL detector and change the lights accordingly

        // Offload the dispatcher to a different thread & start it up
        new Thread(dispatcher, "LightJockey - Audio Dispatching").start()
    }

    @Override
    boolean process(AudioEvent audioEvent) {
        if (splDetector.getCurrentSPL() > threshold) {
            log.info("Sound detected above threshold: ${splDetector.getCurrentSPL() as Integer} dB SPL")

            sleep(randomIntBetween(0, 300))

            // Dynamic light changing (via microphone input)
            randomLightToRandomColor()
//            allLightsToRandomColors()

            // Static light changing
//            randomFlashMode()
//            discoMode()
        }
        return true
    }

    @Override
    void processingFinished() {
        log.info("Processing finished.")
    }

    private void allLightsToRandomColors() {
        hueSDK.selectedBridge.resourceCache.allLights.each { PHLight light ->
            def newState = new PHLightState(
                    hue: randomIntBetween(0, 65535),
                    saturation: randomIntBetween(200, 254),
                    brightness: randomIntBetween(100, 200)
            )
            hueSDK.selectedBridge.updateLightState(light, newState)
        }
    }

    private void randomLightToRandomColor() {
//        int randomIndex = new Random().nextInt(hueSDK.selectedBridge.resourceCache.allLights.size())
        // Hack to ensure only the lights in my bedroom change
        int randomIndex = new Random().nextInt(3)
        randomIndex = randomIndex == 2 ? 3 : randomIndex

        PHLight light = hueSDK.selectedBridge.resourceCache.allLights[randomIndex]
        def newState = new PHLightState(
                hue: randomIntBetween(0, 65535),
                saturation: randomIntBetween(200, 254),
                brightness: randomIntBetween(100, 200)
        )

        hueSDK.selectedBridge.updateLightState(light, newState)
    }

    private void discoMode() {
        PHBridgeResourcesCache cache = hueSDK.selectedBridge.resourceCache
        Map<String, Boolean> isOnMap = [:].withDefault { false }

        while (true) {
//            sleep(500)
            boolean shouldSyncColor = randomIntBetween(1, 20) > 19
            if(shouldSyncColor) {
                def syncedLightState = new PHLightState(
                        brightness: 30,
                        hue: randomIntBetween(0, 65535),
                        transitionTime: 0,
                        on: true
                )
                cache.allLights.each { PHLight light ->
                    hueSDK.selectedBridge.updateLightState(light, syncedLightState)
                }
                sleep(400)
                syncedLightState.on = false
                cache.allLights.each { PHLight light ->
                    hueSDK.selectedBridge.updateLightState(light, syncedLightState)
                }
                continue
            }

            List<PHLight> lights = cache.allLights.clone() as List<PHLight>
            Collections.shuffle(lights)
            lights.each { PHLight light ->
                sleep(1000)
                def lightState = new PHLightState(
                        brightness: randomIntBetween(50, 100),
                        hue: randomIntBetween(40000, 65535),
                        saturation: randomIntBetween(200, 254),
                        transitionTime: 50
                )
                lightState.on = !isOnMap[light.identifier]
                isOnMap[light.identifier] = !isOnMap[light.identifier]
                hueSDK.selectedBridge.updateLightState(light, lightState)
                sleep(randomIntBetween(10, 80))
                lightState.on = !isOnMap[light.identifier]
                isOnMap[light.identifier] = !isOnMap[light.identifier]
                hueSDK.selectedBridge.updateLightState(light, lightState)
            }
        }
    }

    private void randomFlashMode() {
        PHBridgeResourcesCache cache = hueSDK.selectedBridge.resourceCache
        Map<String, Boolean> isOnMap = [:].withDefault { false }
        while (true) {
            sleep(500)

            boolean shouldSyncColor = randomIntBetween(1, 20) > 19
            if(shouldSyncColor) {
                def syncedLightState = new PHLightState(
                        brightness: 30,
                        hue: randomIntBetween(0, 65535),
                        transitionTime: 0,
                        on: true
                )
                hueSDK.selectedBridge.setLightStateForDefaultGroup(syncedLightState)
                continue
            }

            cache.allLights.each { PHLight light ->
                // 5% chance to skip this light
                boolean shouldSkipThisLight = randomIntBetween(1, 20) > 19
                // 10% chance to turn off
                boolean shouldTurnLightOff = randomIntBetween(1, 10) > 7 && isOnMap[light.identifier]

                if(shouldSkipThisLight) return

                def lightState = new PHLightState(
                        brightness: randomIntBetween(50, 100),
                        hue: randomIntBetween(40000, 65535),
                        transitionTime: 0//randomIntBetween(0, 30)
                )

                // If it's on and we should turn it off, turn it off.  Otherwise if it's off, turn it on.
                boolean lightIsOn = isOnMap[light.identifier]
                if(lightIsOn && shouldTurnLightOff) {
                    lightState.on = false
                    isOnMap[light.identifier] = false
                } else if (!lightIsOn) {
                    lightState.on = true
                    isOnMap[light.identifier] = true
                }

                hueSDK.selectedBridge.updateLightState(light, lightState)
            }
        }
    }

    /**
     * Returns random int between two bounds inclusive.
     */
    private static int randomIntBetween(int lowerBound, int upperBound) {
        // Since "nextInt(upperBound - lowerBound) + lowerBound" is inclusive on lowerBound
        // and exclusive on upperBound, we add one to upperBound to make it inclusive.
        int realUpperBound = upperBound + 1
        return new Random().nextInt(realUpperBound - lowerBound) + lowerBound
    }
}
