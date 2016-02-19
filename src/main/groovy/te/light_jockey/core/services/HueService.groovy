package te.light_jockey.core.services

import groovy.util.logging.Slf4j
import te.light_jockey.core.HueTransitionPayloadBuilder
import te.light_jockey.core.domain.hue.HueTransitionProperties
import wslite.rest.ContentType
import wslite.rest.RESTClient

@Slf4j
class HueService {
    public static final Map TO_BRIGHT_WHITE = [on: true, sat: 0, bri: 125, hue: 10_000, transitionTime: 5000]

    final HueTransitionPayloadBuilder hueTransitionPayloadBuilder = new HueTransitionPayloadBuilder()
    final RESTClient hueApiEndpoint

    HueService(String hueBridgeUrl) {
        hueApiEndpoint = new RESTClient(hueBridgeUrl)
    }

    void finalTransition(List<String> lightIds) {
        log.info("\nTransitioning lights to white & exiting program.")
        transitionAllLightsWithSamePayload(lightIds, [on: false])
        sleep(500)
        transitionAllLightsWithSamePayload(lightIds, TO_BRIGHT_WHITE)
    }

    void transitionAllLightsWithDiffPayloads(List<String> lightIds, HueTransitionProperties hueTransitionProperties) {
        log.info("\rTransitioning lights now.")
        lightIds.each { String lightId ->
            Map payload = hueTransitionPayloadBuilder.buildPayloadAsMap(hueTransitionProperties)
            transitionLight(lightId, payload)
        }
    }

    void transitionAllLightsWithSamePayload(List<String> lightIds, HueTransitionProperties hueTransitionProperties) {
        Map payload = hueTransitionPayloadBuilder.buildPayloadAsMap(hueTransitionProperties)
        transitionAllLightsWithSamePayload(lightIds, payload)
    }

    void transitionAllLightsWithSamePayload(List<String> lightIds, Map payload) {
        lightIds.each { String lightId ->
            transitionLight(lightId, payload)
        }
    }

    void transitionLight(String lightId, Map payload) {
        hueApiEndpoint.put(path: "/lights/$lightId/state") {
            type ContentType.JSON
            charset "UTF-8"
            json payload
        }
    }
}
