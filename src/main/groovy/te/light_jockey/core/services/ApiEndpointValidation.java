package te.light_jockey.core.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wslite.rest.RESTClient;

interface ApiEndpointValidation {
    Logger log = LoggerFactory.getLogger(ApiEndpointValidation.class);

    RESTClient getApiEndpoint();

    /**
     * Makes a HEAD request to the base URL of the api endpoint to ensure it
     * exists and is responding to requests.
     *
     * @return true iff the api endpoint responds to the HEAD request with a 200/OK
     */
    default boolean endpointReturns200ForHeadRequest() {
        try {
            getApiEndpoint().head();
        } catch (Exception exception) {
            String serviceName = this.getClass().getSimpleName().replace("Service", "");
            log.error("::: Could not connect to the " + serviceName + " endpoint - please check the settings file :::");
            return false;
        }
        return true;
    }
}