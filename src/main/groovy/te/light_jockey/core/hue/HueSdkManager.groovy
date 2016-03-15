package te.light_jockey.core.hue

import com.philips.lighting.hue.sdk.PHBridgeSearchManager
import com.philips.lighting.hue.sdk.PHHueSDK
import com.philips.lighting.hue.sdk.PHSDKListener
import com.philips.lighting.hue.sdk.heartbeat.PHHeartbeatManager
import groovy.util.logging.Slf4j

/**
 * This is a wrapper around the PHHueSDK class, and other Philips Hue classes, abstracting away the
 * necessary startup/shutdown logic (along with other SDK features).
 */
@Slf4j
class HueSDKManager {

    public static final PHHueSDK SDK = PHHueSDK.create()

    static void initializeSDK() {
        SDK.with {
            setAppName("light-jockey")
            setDeviceName("desktop-application")
        }
        registerShutdownHook()
    }


    static void registerSDKListener(PHSDKListener listener) {
        SDK.notificationManager.registerSDKListener(listener)
    }

    /**
     * This triggers a UPNP/Portal search on the LAN for all Hue bridges.
     * <p>The search can take up to 10 seconds to complete.
     * <p>Upon completion of the search, the PHSDKListener.onBridgeConnected() method will be called.
     */
    static void triggerBridgeSearch() {
        log.info("Searching for Hue bridges on the LAN...")
        def bridgeSearchManager = SDK.getSDKService(PHHueSDK.SEARCH_BRIDGE) as PHBridgeSearchManager
        bridgeSearchManager.search(true, true)  // void search(isUpnpSearch, isPortalSearch)
    }

    /**
     * This calls the necessary methods to gracefully terminate the SDK and its connection to the Hue bridge.
     * <p>If this method is called after the SDK has already been shutdown it does nothing.
     */
    static void shutdownSDK() {
        // Check if the SDK has been destroyed already or not
        boolean isNotAlreadyShutdown = SDK.@instance as Boolean
        if(isNotAlreadyShutdown) {
            log.info("Shutting down the Hue connection.")
            shutdown()
        }
    }

    private static void shutdown() {
        PHHeartbeatManager.instance.disableAllHeartbeats(SDK.selectedBridge)    // Disable all heartbeats to the bridge
        SDK.disconnect(SDK.selectedBridge)                                      // Disconnect from the bridge
        SDK.destroySDK()                                                        // Nullify the SDK object reference
    }

    private static void registerShutdownHook() {
        addShutdownHook { shutdownSDK() }
    }
}
