package te.light_jockey

import te.light_jockey.core.LightJockey
import te.light_jockey.core.hue.HueBridgeService

class Startup {

    public static void main(String... args) {
        def hueBridgeService = HueBridgeService.createWithBridgeConnectionCallback("LightJockey") {
            new LightJockey().start()
        }

        hueBridgeService.findAndConnectToBridge()
    }

}
