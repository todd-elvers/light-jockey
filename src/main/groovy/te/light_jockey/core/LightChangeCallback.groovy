package te.light_jockey.core

import com.philips.lighting.hue.listener.PHLightListener
import com.philips.lighting.model.PHBridgeResource
import com.philips.lighting.model.PHHueError
import com.philips.lighting.model.PHLight
import groovy.util.logging.Slf4j

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
