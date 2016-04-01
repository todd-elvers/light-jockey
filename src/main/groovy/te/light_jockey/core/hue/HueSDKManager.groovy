package te.light_jockey.core.hue

import com.philips.lighting.hue.sdk.*
import com.philips.lighting.hue.sdk.heartbeat.PHHeartbeatManager
import com.philips.lighting.hue.sdk.notification.impl.PHNotificationManagerImpl
import groovy.util.logging.Slf4j
import te.light_jockey.core.ConfigHandler

import static ConfigHandler.IP_ADDRESS_PROP
import static ConfigHandler.USERNAME_PROP
import static java.lang.System.getProperty

/**
 * This is a wrapper around the PHHueSDK class, and other Philips Hue classes, abstracting away the
 * necessary startup/shutdown logic (along with other SDK features).
 */
@Slf4j
class HueSDKManager {

    private static final ConfigHandler configHandler = ConfigHandler.getInstance()
    private static final PHHueSDK hueSDK = PHHueSDK.getInstance()

    static void initSDKIfNecessary(String appName) {
        hueSDK.appName = hueSDK.appName ?: appName
        hueSDK.deviceName = hueSDK.deviceName ?: "${getProperty('user.name')}@${getProperty('os.name')}"
        addShutdownHook { shutdownSDK() }
    }

    static void registerSDKListener(PHSDKListener listener) {
        PHNotificationManager notificationManager = hueSDK.notificationManager as PHNotificationManagerImpl
        boolean thisListenerIsNotAlreadyRegistered = !notificationManager.localSDKListeners.contains(listener)
        if(thisListenerIsNotAlreadyRegistered){
            hueSDK.notificationManager.registerSDKListener(listener)
        }
    }

    static boolean configFileHasValidCredentials() {
        configHandler.configFileExists() && configHandler.configFileIsStillValid()
    }

    static void createConfigFileIfNecessary() {
        if(!configHandler.configFileExists()) configHandler.createConfigFile()
    }

    static void connectToBridgeUsingConfigFileCredentials() {
        log.debug("Using Hue credentials from config file.")
        Properties props = configHandler.readConfigProperties()
        PHAccessPoint accessPoint = new PHAccessPoint(
                ipAddress: props.getProperty(IP_ADDRESS_PROP),
                username : props.getProperty(USERNAME_PROP)
        )
        hueSDK.connect(accessPoint)
    }

    /**
     * This triggers a UPNP/Portal search on the LAN for all Hue bridges.
     * <p>The search can take up to 10 seconds to complete.
     * <p>Upon completion of the search, the PHSDKListener.onBridgeConnected() method will be called.
     */
    static void triggerBridgeSearchOverLAN() {
        log.info("Searching for Hue bridges on the LAN...")
        def bridgeSearchManager = hueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE) as PHBridgeSearchManager
        bridgeSearchManager.search(true, true)  // void search(isUpnpSearch, isPortalSearch)
    }

    /**
     * This calls the necessary methods to gracefully terminate the SDK and its connection to the Hue bridge.
     * <p>If this method is called after the SDK has already been shutdown it does nothing.
     */
    static void shutdownSDK() {
        // Check if the SDK has been destroyed already or not
        boolean isNotAlreadyShutdown = hueSDK.@instance as Boolean
        if (isNotAlreadyShutdown) {
            log.info("Shutting down the Hue connection.")
            shutdown()
        }
    }

    private static void shutdown() {
        PHHeartbeatManager.instance.disableAllHeartbeats(hueSDK.selectedBridge)
        hueSDK.disconnect(hueSDK.selectedBridge)
        hueSDK.destroySDK()
    }

}
