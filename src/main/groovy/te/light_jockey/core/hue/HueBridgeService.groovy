package te.light_jockey.core.hue

import te.light_jockey.core.hue.callbacks.BridgeConnectedCallback

class HueBridgeService {

    HueBridgeService(String appName, BridgeConnectedCallback callback) {
        HueSDKEventListener sdkListener = new HueSDKEventListener(callback)
        HueSDKManager.initSDKIfNecessary(appName)
        HueSDKManager.registerSDKListener(sdkListener)
        HueSDKManager.createConfigFileIfNecessary()
    }

    void findAndConnectToBridge() {
        if(HueSDKManager.configFileHasValidCredentials()) {
            HueSDKManager.connectToBridgeUsingConfigFileCredentials()
        } else {
            HueSDKManager.triggerBridgeSearchOverLAN()
        }
    }

}
