package te.light_jockey

import groovy.transform.CompileStatic
import te.light_jockey.core.LightJockey
import te.philips_hue.HueBridgeService

@CompileStatic
class Startup {

    public static void main(String... args) {
        def hueBridgeService = HueBridgeService.createWithBridgeConnectionCallback("LightJockey") {
            new LightJockey().start()
        }

        hueBridgeService.findAndConnectToBridge()
    }

}
