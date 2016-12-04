package te.light_jockey.core

import com.philips.lighting.hue.sdk.PHHueSDK
import com.philips.lighting.model.PHBridgeResourcesCache
import com.philips.lighting.model.PHLight
import com.philips.lighting.model.PHLightState
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.jnativehook.keyboard.NativeKeyEvent
import te.light_jockey.core.audio_processing.SoundPressureLevelDetector

import static te.light_jockey.core.io.PropertiesFileReader.readAppProperty

@Slf4j
@CompileStatic
class KeyboardLightJockey extends HookRegisteringKeyListener {
    private static final PHLightState INSTANT_OFF = new PHLightState(on: false, transitionTime: 0)
    private static final PHLightState FADE_OFF = new PHLightState(on: false)

    PHHueSDK hueSDK = PHHueSDK.getInstance()

    @Override
    void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        super.nativeKeyPressed(nativeKeyEvent)
        println nativeKeyEvent.keyCode

        PHLight livingRoom1 = hueSDK.selectedBridge.resourceCache.allLights[7]
        PHLight livingRoom2 = hueSDK.selectedBridge.resourceCache.allLights[12]
        PHLight livingRoom3 = hueSDK.selectedBridge.resourceCache.allLights[1]
        List<PHLight> lights = [livingRoom1, livingRoom2, livingRoom3]

        // 13 lights right now
        switch (nativeKeyEvent.getKeyCode()) {
            case NativeKeyEvent.VC_KP_1:
                updateLight(livingRoom1, randomColor())
                break
            case 3663:
                turnOff(livingRoom1)
                break


            case NativeKeyEvent.VC_KP_2:
                updateLight(livingRoom2, randomColor())
                break
            case 57424:
                turnOff(livingRoom2)
                break


            case NativeKeyEvent.VC_KP_3:
                updateLight(livingRoom3, randomColor())
                break
            case 3665:
                turnOff(livingRoom3)
                break


            case NativeKeyEvent.VC_KP_4:
                def newColor = randomColor()
                lights.each {
                    updateLight(it, newColor)
                }
                break
            case 57419:
                lights.each {
                    turnOff(it)
                }
                break
        }
    }

    private void turnOff(PHLight light) {
        hueSDK.selectedBridge.updateLightState(light, INSTANT_OFF)
    }

    private static PHLightState randomColor() {
        return new PHLightState(
                hue           : randomIntBetween(0, 65535),
                saturation    : 254,//randomIntBetween(200, 254),
                brightness    : randomIntBetween(100, 200),
                transitionTime: randomIntBetween(0, 10)
        )
    }

    private void updateLight(PHLight light, PHLightState newState) {
        if (!light.lastKnownLightState.isOn()) {
            newState.on = true
        }

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

}
