package te.light_jockey.core

import com.philips.lighting.hue.listener.PHLightListener
import com.philips.lighting.hue.sdk.PHHueSDK
import com.philips.lighting.model.PHBridgeResource
import com.philips.lighting.model.PHHueError
import com.philips.lighting.model.PHLight
import com.philips.lighting.model.PHLightState
import com.sun.media.sound.DirectAudioDevice
import groovy.util.logging.Slf4j
import te.light_jockey.core.audio_processing.*

import javax.sound.sampled.*

import static te.light_jockey.misc.PropertiesFileReader.readAppProperty

@Slf4j
class LightJockey implements AudioProcessor {

    double threshold = -50
    PHHueSDK hueSDK = PHHueSDK.getInstance()
    SilenceDetector silenceDetector = new SilenceDetector(threshold, false)

    void start() {
        log.info("Welcome to LightJockey v${readAppProperty('version')}!\n")

        // Find the system's microphone
        Mixer microphone = getRecordingDevices().first()

        // Open a connection to the microphone
        int bufferSize = 512
        AudioFormat format = new AudioFormat(44100, 16, 1, true, true)
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine, format)
        TargetDataLine line = (TargetDataLine) microphone.getLine(dataLineInfo)
        line.open(format, bufferSize)   // bufferSize == numSamples??
        line.start()
        AudioInputStream stream = new AudioInputStream(line)
        JVMAudioInputStream audioStream = new JVMAudioInputStream(stream)

        // Create an audio dispatcher to dispatch data from the microphone to audio processor(s)
        AudioDispatcher dispatcher = new AudioDispatcher(audioStream, bufferSize, 0)

        // Add our silence detector to translate the raw microphone data to dB SPL
        dispatcher.addAudioProcessor(silenceDetector)

        // Add our LightJockey processor react to the dB SPL reported by our silence detector
        dispatcher.addAudioProcessor(this)

        // Offload the dispatcher to a different thread & start it up
        new Thread(dispatcher, "LightJockey - Audio Dispatching").start()
    }

    @Override
    boolean process(AudioEvent audioEvent) {
        if (silenceDetector.currentSPL() > threshold) {
            log.info("Sound detected above threshold: ${silenceDetector.currentSPL() as Integer} dB SPL")

            sleep(randomIntBetween(0, 300))

            changeRandomLight()
//            changeAllLightsRandomly()
        }
        return true
    }

    @Override
    void processingFinished() {
        log.info("Processing finished.")
    }


    public static List<Mixer> getRecordingDevices() {
        return AudioSystem.mixerInfo.toList().findResults { Mixer.Info info ->
            def recordingDevice = AudioSystem.getMixer(info)

            //TODO: Is this safe to do?  Are the "PortMixer" references truly noise?
            if (recordingDevice.class == DirectAudioDevice) {
                AudioSystem.getMixer(info).targetLineInfo ? AudioSystem.getMixer(info) : null
            }
        }
    }

    private void changeAllLightsRandomly() {
        hueSDK.selectedBridge.resourceCache.allLights.each { PHLight light ->
            def newState = new PHLightState(
                    hue: randomIntBetween(0, 65535),
                    saturation: randomIntBetween(200, 254),
                    brightness: randomIntBetween(100, 200),
//                    transitionTime  : randomIntBetween(0, 10)
            )
            hueSDK.selectedBridge.updateLightState(light, newState)
        }
    }

    private void changeRandomLight() {
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

    /**
     * Returns random int between two bounds inclusive.
     */
    private static int randomIntBetween(int lowerBound, int upperBound) {
        // Since "nextInt(upperBound - lowerBound) + lowerBound" is inclusive on lowerBound
        // and exclusive on upperBound, we add one to upperBound to make it inclusive.
        int realUpperBound = upperBound + 1
        return new Random().nextInt(realUpperBound - lowerBound) + lowerBound
    }

    @Slf4j
    class LightChangeCallback implements PHLightListener {

        @Override
        void onReceivingLightDetails(PHLight phLight) {
            log.info("Receiving light details:\n\t${phLight.dump()}")
        }

        @Override
        void onReceivingLights(List<PHBridgeResource> list) {
            log.info("Receiving lights:\n\t${list*.dump()}")
        }

        @Override
        void onSearchComplete() {
            log.info("Search complete.")
        }

        @Override
        void onSuccess() {
            log.info("Search complete.")
        }

        @Override
        void onError(int i, String s) {
            log.error("Error #$i: $s")
        }

        // This method indicates that the state change was successful.
        @Override
        void onStateUpdate(Map<String, String> map, List<PHHueError> list) {
            log.info("State updated!")
        }
    }

}
