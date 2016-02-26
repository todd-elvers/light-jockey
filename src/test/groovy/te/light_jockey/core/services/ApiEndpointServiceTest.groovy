package te.light_jockey.core.services

import spock.lang.Specification
import wslite.http.HTTPRequest
import wslite.http.HTTPResponse
import wslite.rest.RESTClient
import wslite.rest.RESTClientException

class ApiEndpointServiceTest extends Specification {

    def apiEndpointService = [
            getApiEndpoint: {
                return Mock(RESTClient)
            }
    ] as ApiEndpointService


    def "when the API endpoint returns a non-200: handle the RESTClientException gracefully"() {
        given: 'an exception due to a non-200 response'
            apiEndpointService.apiEndpoint.put(_ as Map, _ as Closure) >> {
                throw new RESTClientException("Endpoint error", Mock(HTTPRequest), Mock(HTTPResponse))
            }
            println apiEndpointService.class

        when:
            apiEndpointService.get(path: 'some_path')

        then: 'the exception did not bubble up'
            noExceptionThrown()

    }

}
