package te.light_jockey.core.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import wslite.rest.RESTClient;

interface ApiEndpointValidation {
    Logger log = LoggerFactory.getLogger(ApiEndpointValidation.class);

    RESTClient getApiEndpoint();

    /**
     * Ensures the API endpoint is available and accepting requests.
     *
     * <p>This is done by making a GET request to the base URL of the API endpoint and checking
     * if the response is a 200/OK.
     *
     * @return true iff the api endpoint responds to the GET request with a 200/OK
     */
    default boolean isApiEndpointAvailable() {
        try {
            getApiEndpoint().get(availabilityCheckParams());
        } catch (Exception exception) {
            String serviceName = this.getClass().getSimpleName().replace("Service", "");
            log.error("::: Could not connect to the " + serviceName + " endpoint - please check the settings file :::");
            return false;
        }
        return true;
    }

	/**
	 * @return the request parameters to use when making the API endpoint availability check.
	 */
	default Map<String, Object> availabilityCheckParams() {
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put("connectTimeout", 1000);
		requestParams.put("readTimeout", 1000);
		return requestParams;
	}
}