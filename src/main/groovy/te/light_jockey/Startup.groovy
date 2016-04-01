package te.light_jockey

import te.light_jockey.core.hue.HueBridgeService

class Startup {

    public static void main(String... args) {
        def bridgeConnectedCallback = { new LightJockey().start() }
        def hueBridgeService = new HueBridgeService("LightJockey", bridgeConnectedCallback)

        hueBridgeService.findAndConnectToBridge()
    }

}
