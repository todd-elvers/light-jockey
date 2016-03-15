package te.light_jockey

import groovy.util.logging.Slf4j
import te.light_jockey.core.hue.HueSDKEventListener
import te.light_jockey.core.hue.HueSDKManager

import static te.light_jockey.misc.PropertiesFileReader.readAppProperty

@Slf4j
class LightJockey {

    HueSDKEventListener sdkListener = new HueSDKEventListener()

    void start() {
        log.info("Welcome to LightJockey v${readAppProperty('version')}!\n")

        HueSDKManager.initializeSDK()
        HueSDKManager.registerSDKListener(sdkListener)
        if(HueSDKManager.configFileIsValid()) {
            HueSDKManager.connectToBridgeFromConfigFile()
        } else {
            HueSDKManager.configHandler.createConfigFile()
            HueSDKManager.triggerBridgeSearch()
        }


    }
}
