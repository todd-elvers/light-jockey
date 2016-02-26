package te.light_jockey.core.services

import groovy.util.logging.Slf4j
import wslite.rest.ContentType
import wslite.rest.RESTClient
import wslite.rest.RESTClientException
import wslite.rest.Response

@Slf4j
abstract class ApiEndpointService {

    abstract RESTClient getApiEndpoint()

    Optional<Response> get(Map queryParams) {
        Response response

        try {
            response = getApiEndpoint().get(queryParams)
        } catch(RESTClientException exception) {
            int code = exception.response.statusCode
            String message = exception.response.statusMessage
            log.error("Failed to communicate with the ${this.class.simpleName} endpoint ($code:$message).")
        }

        return Optional.ofNullable(response)
    }

    void put(Map queryParams, Map payload) {
        try {
            getApiEndpoint().put(queryParams) {
                type ContentType.JSON
                charset "UTF-8"
                json payload
            }
        } catch (RESTClientException exception) {
            int code = exception.response.statusCode
            String message = exception.response.statusMessage
            log.info("Failed to communicate with the ${this.class.simpleName} endpoint ($code:$message).")
        }
    }
}
