package te.light_jockey.core.services

import spock.lang.Specification
import spock.lang.Unroll

class SonosServiceTest extends Specification {

    @Unroll("urlEncode(#input) == #output")
    def "urlEncode() can properly url encode strings"() {
        expect:
            SonosService.urlEncode(input) == output
        where:
            input               | output
            'room'              | 'room'
            'living room'       | 'living%20room'
            'really cool-name!' | 'really%20cool-name%21'
    }
}
