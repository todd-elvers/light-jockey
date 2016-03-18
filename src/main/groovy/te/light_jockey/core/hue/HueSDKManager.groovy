package te.light_jockey.core.hue

import com.philips.lighting.hue.sdk.PHAccessPoint
import com.philips.lighting.hue.sdk.PHBridgeSearchManager
import com.philips.lighting.hue.sdk.PHHueSDK
import com.philips.lighting.hue.sdk.PHSDKListener
import com.philips.lighting.hue.sdk.heartbeat.PHHeartbeatManager
import groovy.util.logging.Slf4j
import te.light_jockey.core.ConfigHandler

import static ConfigHandler.IP_ADDRESS_PROP
import static ConfigHandler.USERNAME_PROP

/**
 * This is a wrapper around the PHHueSDK class, and other Philips Hue classes, abstracting away the
 * necessary startup/shutdown logic (along with other SDK features).
 */
@Slf4j
class HueSDKManager {

    public static final ConfigHandler configHandler = ConfigHandler.getInstance()
    public static final PHHueSDK SDK = PHHueSDK.getInstance()

    static void initializeSDK() {
        SDK.setAppName("light-jockey")
        SDK.setDeviceName("desktop-application")
        addShutdownHook { shutdownSDK() }
    }


    static void registerSDKListener(PHSDKListener listener) {
        SDK.notificationManager.registerSDKListener(listener)
    }

    static boolean configFileIsValid() {
        configHandler.configFileExists() && configHandler.configFileIsStillValid()
    }

    static void connectToBridgeFromConfigFile() {
        log.debug("Using Hue credentials from config file.")
        Properties props = configHandler.readConfigProperties()
        PHAccessPoint accessPoint = new PHAccessPoint(
                ipAddress: props.getProperty(IP_ADDRESS_PROP),
                username : props.getProperty(USERNAME_PROP)
        )
        SDK.connect(accessPoint)
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
        if (isNotAlreadyShutdown) {
            log.info("Shutting down the Hue connection.")
            shutdown()
        }
    }

    private static void shutdown() {
        PHHeartbeatManager.instance.disableAllHeartbeats(SDK.selectedBridge)
        SDK.disconnect(SDK.selectedBridge)
        SDK.destroySDK()
    }

}
