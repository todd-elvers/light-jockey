package te.light_jockey.core

import com.philips.lighting.hue.sdk.PHHueSDK
import com.philips.lighting.model.PHLight
import com.philips.lighting.model.PHLightState
import spock.lang.Specification
import te.philips_hue.HueBridgeService
import te.philips_hue.callbacks.BridgeConnectedCallback


class PhilipsHueFeatures extends Specification {

    def "can use resource cache to find lights that are off"() {
        given:
            def bridgeCallback = {
                printDivider()

                // Gather the lights that are currently off
                List<PHLight> lightsThatAreOff = []
                PHHueSDK.instance.selectedBridge.resourceCache.allLights.each { PHLight light ->
                    println("Light: $light.name is ${light.lastKnownLightState.on ? 'on' : 'off'}")
                    if (!light.getLastKnownLightState().isOn()) {
                        lightsThatAreOff << light
                    }
                }
                println("The lights that are off:\n${lightsThatAreOff*.name.join('\n')}")

                // Turn them on
                def turnedOn = new PHLightState(on: true)
                lightsThatAreOff.each {
                    PHHueSDK.instance.selectedBridge.updateLightState(it, turnedOn)
                }
            }

        expect:
            executeTheBridgeCallbackAndReturnTrue(bridgeCallback)
    }

    def "can flicker individual lights simultaneously"() {
        given:
            def bridgeCallback = {
                printDivider()

                List<PHLight> allLights = PHHueSDK.instance.selectedBridge.resourceCache.allLights.clone()

                def originalBrightness = allLights.first().lastKnownLightState.brightness

                def turnedOn = new PHLightState(on: true, transitionTime: 0, brightness: originalBrightness)
                def turnedOff = new PHLightState(on: false, transitionTime: 0)
                while (true) {
                    Collections.shuffle(allLights)
                    allLights.each {
                        PHHueSDK.instance.selectedBridge.updateLightState(it, turnedOn)
                    }
                    sleep(400)
                    allLights.each {
                        PHHueSDK.instance.selectedBridge.updateLightState(it, turnedOff)
                    }
                }

            }

        expect:
            executeTheBridgeCallbackAndReturnTrue(bridgeCallback, 5_000)
    }

    def "can flicker individual lights in order"() {
        given:
            def bridgeCallback = {
                ensureAllLightsAreOn()
                printDivider()

                List<PHLight> allLights = PHHueSDK.instance.selectedBridge.resourceCache.allLights

                def turnedOn = new PHLightState(on: true, transitionTime: 0)
                def turnedOff = new PHLightState(on: false, transitionTime: 0)
                while (true) {
                    allLights.each {
                        int originalBrightness = it.lastKnownLightState.brightness

                        PHHueSDK.instance.selectedBridge.updateLightState(it, turnedOff)
                        sleep(400)
                        turnedOn.brightness = originalBrightness
                        PHHueSDK.instance.selectedBridge.updateLightState(it, turnedOn)
                    }
                    allLights.reverse(true)
                }

            }

        expect:
            executeTheBridgeCallbackAndReturnTrue(bridgeCallback, 8_000)
    }

    def "can flicker groups of lights"() {
        given:
            def bridgeCallback = {
                ensureAllLightsAreOn()
                printDivider()

                def turnedOn = new PHLightState(on: true, transitionTime: 30, brightness: 200)
                def turnedOff = new PHLightState(on: false, transitionTime: 30)
                while (true) {
                    PHHueSDK.instance.selectedBridge.setLightStateForDefaultGroup(turnedOff)
                    sleep(4000)
                    PHHueSDK.instance.selectedBridge.setLightStateForDefaultGroup(turnedOn)
                }

            }

        expect:
            executeTheBridgeCallbackAndReturnTrue(bridgeCallback, 8_000)
    }

    private static void ensureAllLightsAreOn() {
        print "Ensuring all lights are on..."
        def turnOnQuicklyAndBrightly = new PHLightState(on: true, transitionTime: 0, brightness: 200)
        PHHueSDK.instance.selectedBridge.setLightStateForDefaultGroup(turnOnQuicklyAndBrightly)
        println "Done."
        sleep(500)
    }

    private static void printDivider() {
        println("-" * 50)
    }

    private static boolean executeTheBridgeCallbackAndReturnTrue(BridgeConnectedCallback callback, int callbackTimeout = 1_000) {
        HueBridgeService.createWithBridgeConnectionCallback("light-jockey-testing", callback).findAndConnectToBridge()
        sleep(callbackTimeout)
        return true
    }
}