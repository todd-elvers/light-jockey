package te.light_jockey.core.hue

import com.philips.lighting.hue.sdk.PHAccessPoint
import com.philips.lighting.hue.sdk.PHHueSDK
import com.philips.lighting.hue.sdk.PHMessageType
import com.philips.lighting.hue.sdk.PHSDKListener
import com.philips.lighting.hue.sdk.heartbeat.PHHeartbeatManager
import com.philips.lighting.model.PHBridge
import com.philips.lighting.model.PHHueParsingError
import groovy.util.logging.Slf4j
import te.light_jockey.core.ConfigHandler

@Slf4j
class HueSDKEventListener implements PHSDKListener {

    boolean authenticationWasRequired = false
    int pushlinkButtonTimeoutCounter = 0
    PHHueSDK hueSDK = PHHueSDK.getInstance()
    ConfigHandler configHandler = ConfigHandler.getInstance()

    //TODO: Handle multiple access points being returned
    @Override
    void onAccessPointsFound(List<PHAccessPoint> accessPoints) {
        def accessPoint = accessPoints.first()
        log.info("Access point found! (IP=$accessPoint.ipAddress)")
        resetPushlinkButtonTimeoutCounter()
        hueSDK.connect(accessPoint)
    }

    private void resetPushlinkButtonTimeoutCounter() {
        pushlinkButtonTimeoutCounter = 30
    }

    @Override
    void onCacheUpdated(List<Integer> cacheNotifications, PHBridge bridge) {
        if (cacheNotifications.contains(PHMessageType.LIGHTS_CACHE_UPDATED)) {
            log.info("Lights Cache Updated")
        }
    }

    //TODO: Pass control to application
    //TODO: Store IP & username somewhere
    /**
     * This callback is triggered once bridge authentication completes and is passed the username that the bridge generated for us.
     * <p>This finishes the bridge-connection process and sets up a heartbeat to the bridge every 10 seconds.
     * 
     * @param bridge the bridge we just connected to
     * @param username the username to use when making API requests to this bridge
     */
    @Override
    void onBridgeConnected(PHBridge bridge, String username) {
        hueSDK.setSelectedBridge(bridge)
        PHHeartbeatManager.instance.enableLightsHeartbeat(bridge, PHHueSDK.HB_INTERVAL)
        log.info("\rBridge Connected!")
        log.debug("Bridge details:\n\tUsername: $username\n\tLights: ${bridge.getResourceCache().getAllLights()*.identifier.join(',')}")
        if(authenticationWasRequired) {
            authenticationWasRequired = false
            configHandler.updateConfigFile([
                    (ConfigHandler.USERNAME_PROP)  : username,
                    (ConfigHandler.IP_ADDRESS_PROP): bridge.getResourceCache().getBridgeConfiguration().getIpAddress()
            ])
        }
    }

    /**
     * This callback is triggered when a bridge requires authentication.
     * <p>Authentication just means pressing the pushlink button on top of the bridge within 30 seconds.
     *
     * @param accessPoint the access point requesting authentication via the pushlink button
     */
    @Override
    void onAuthenticationRequired(PHAccessPoint accessPoint) {
        log.info("Authentication required!  Please press the blue button on the Hue Bridge.")
        hueSDK.startPushlinkAuthentication(accessPoint)
        authenticationWasRequired = true
    }

    @Override
    void onConnectionResumed(PHBridge bridge) {
//        log.info("Connection to the bridge has resumed.")
    }

    @Override
    void onConnectionLost(PHAccessPoint accessPoint) {
        log.info("Connection to bridge lost!")
    }

    @Override
    void onError(int code, final String message) {
        switch (code) {
            case PHMessageType.PUSHLINK_BUTTON_NOT_PRESSED:
                log.info("\r${pushlinkButtonTimeoutCounter--} seconds remaining to press the blue button...")
                break

            default:
                log.error("($code) $message")
        }
    }

    /**
     * Any JSON parsing errors that occurred will be passed to this method.
     */
    @Override
    void onParsingErrors(List<PHHueParsingError> parsingErrors) {
        log.info("Parsing errors!")
    }
}
