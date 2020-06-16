package com.modus.client.it;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.CoreMatchers;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.*;

/**
 * Created by art on 4/20/20.
 */
public class ApplicationCucumberSteps {

    private static final Logger DEFAULT_LOGGER = org.slf4j.LoggerFactory.getLogger(ApplicationCucumberSteps.class);

    private Logger log = DEFAULT_LOGGER;

    //
    // Values from test
    //
    private String applicationBaseUrl = "http://localhost:3000";
    private String postContent;
    private Map<String, String> postHeaders = new TreeMap<>();

    //
    // Runtime state
    //
    private Response response;

    @Given("^base application url in system property \"([^\"]*)\"$")
    public void base_application_url_in_system_property(String properyName) throws Throwable {
        if(System.getProperty(properyName) != null) {
            this.applicationBaseUrl = System.getProperty(properyName);
        }

        this.log.info("USING base application URL {}", this.applicationBaseUrl);
    }

    @Then("^send GET request with path \"([^\"]*)\" to application$")
    public void send_GET_request_with_path_to_application(String path) throws Throwable {
        URL url = this.formUrl(this.applicationBaseUrl, path);

        this.log.info("GET url={}", url);

        this.response = RestAssured.get(url);
    }

    @Then("^verify the HTTP response code is (\\d+)$")
    public void verify_the_HTTP_response_code_is(int expectedHttpCode) throws Throwable {
        assertEquals(expectedHttpCode, this.response.getStatusCode());
    }

    @Then("^verify the body consists of the text \"([^\"]*)\"$")
    public void verify_the_body_consists_of_the_text(String expectedContentText) throws Throwable {
        assertEquals(expectedContentText, this.response.getBody().asString());
    }

    @Then("^verify the body consists the field \"([^\"]*)\"$")
    public void verify_the_body_consists_the_field(String fieldName) {
        assertThat(this.response.getBody().asString(), CoreMatchers.containsString(fieldName));

        JSONObject responseBody = new JSONObject( this.response.getBody().asString());
        String value = responseBody.get(fieldName).toString();

        assertTrue(value != null && value != "null");
    }

    @Given("^POST body \"([^\"]*)\"$")
    public void POST_body(String bodyContent) throws Throwable {
        this.postContent = bodyContent;
    }

    @Given("^POST body$")
    public void post_body(String bodyContent) {
        this.postContent = bodyContent;
    }

    @Then("^send POST request with path \"([^\"]*)\" to application$")
    public void send_POST_request_with_path_to_application(String path) throws Throwable {
        URL url = this.formUrl(this.applicationBaseUrl, path);

        this.log.info("POST url={}", url);

        this.response = RestAssured
                .with()
                .body(this.postContent)
                .headers(this.postHeaders)
                .post(url)
        ;
    }

    @Given("^HTTP header \"([^\"]*)\" = \"([^\"]*)\"$")
    public void HTTP_header_(String headerName, String value) throws Throwable {
        this.postHeaders.put(headerName, value);
    }

//========================================
// Internals
//----------------------------------------

    private URL formUrl(String base, String path) throws MalformedURLException {
        URL baseUrl = new URL(base);
        URL result = new URL(baseUrl, path);

        return result;
    }
}
