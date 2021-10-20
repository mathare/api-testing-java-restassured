package com.typicode.jsonplaceholder.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CommonSteps {

    private final String BASE_RESOURCES_DIR = "src/test/resources/";
    private final String SCHEMAS_DIR = BASE_RESOURCES_DIR + "schemas/";
    private final String EXPECTED_RESPONSES_DIR = BASE_RESOURCES_DIR + "expectedResponses/";

    private RequestSpecification request;
    private Response response;
    private List<Response> responses;

    @Before
    public void setup() {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com/";
        request = RestAssured.given();
        request.header("Content-Type", "application/json");
        responses = new ArrayList<>();
    }

    @When("^I make a (GET|DELETE) request to the (Posts|Comments|Albums|Photos|ToDos|Users) endpoint$")
    public void makeRequest(String requestType, String endpoint) {
        endpoint = endpoint.toLowerCase();
        response = requestType.equals("GET") ?
                request.get(endpoint) :
                request.delete(endpoint);
        responses.add(response);
    }

    @When("^I make a (GET|DELETE) request to the (Posts|Comments|Albums|Photos|ToDos|Users) endpoint with a path parameter of (-?\\d+)$")
    public void makeRequest(String requestType, String endpoint, int pathParam) {
        endpoint = endpoint.toLowerCase();
        response = requestType.equals("GET") ?
                request.get(endpoint + "/" + pathParam) :
                request.delete(endpoint + "/" + pathParam);
        responses.add(response);
    }

    @When("^I make a (POST|PUT) request with an empty body to the (Posts|Comments|Albums|Photos|ToDos|Users) endpoint$")
    public void makeRequestWithEmptyBody(String requestType, String endpoint) {
        endpoint = endpoint.toLowerCase();
        request.body("{}");
        response = requestType.equals("POST") ?
                request.post(endpoint) :
                request.put(endpoint);
        responses.add(response);
    }

    @When("^I make a (POST|PUT) request with an empty body to the (Posts|Comments|Albums|Photos|ToDos|Users) endpoint with a path parameter of (-?\\d+)$")
    public void makeRequestWithEmptyBody(String requestType, String endpoint, int pathParam) {
        endpoint = endpoint.toLowerCase();
        request.body("{}");
        response = requestType.equals("POST") ?
                request.post(endpoint + "/" + pathParam) :
                request.put(endpoint + "/" + pathParam);
        responses.add(response);
    }

    @When("^I make a (POST|PUT) request with the following body to the (Posts|Comments|Albums|Photos|ToDos|Users) endpoint$")
    public void makeRequestWithBody(String requestType, String endpoint, DataTable dataTable) {
        endpoint = endpoint.toLowerCase();
        Map<String, String> requestBodyMap = dataTable.rows(1).asMap(String.class, String.class);
        request.body(buildJsonString(requestBodyMap));
        response = requestType.equals("POST") ?
                request.post(endpoint) :
                request.put(endpoint);
        responses.add(response);
    }

    @When("^I make a (POST|PUT) request with the following body to the (Posts|Comments|Albums|Photos|ToDos|Users) endpoint with a path parameter of (-?\\d+)$")
    public void makeRequestWithBody(String requestType, String endpoint, int pathParam, DataTable dataTable) {
        endpoint = endpoint.toLowerCase();
        Map<String, String> requestBodyMap = dataTable.subTable(1, 0).asMap(String.class, String.class);
        request.body(buildJsonString(requestBodyMap));
        response = requestType.equals("POST") ?
                request.post(endpoint + "/" + pathParam) :
                request.put(endpoint + "/" + pathParam);
        responses.add(response);
    }

    @When("^I make a GET request to the (Posts|Comments|Albums|Photos|ToDos|Users) endpoint with an? \"(.*)\" query parameter of (.*)$")
    public void makeGetRequestWithQueryParameter(String endpoint, String key, String value) {
        endpoint = endpoint.toLowerCase();
        Map<String, String> params = new HashMap<>();
        params.put(key, value);
        response = request.queryParams(params).get(endpoint);
        responses.add(response);
    }

    @When("^I make a GET request to the (Posts|Comments|Albums|Photos|ToDos|Users) endpoint with nested path parameters of (-?\\d+\\/\\w+)$")
    public void makeRequestWithNestedParameters(String endpoint, String nestedParam) {
        endpoint = endpoint.toLowerCase();
        response = request.get(endpoint + "/" + nestedParam);
        responses.add(response);
    }

    @Then("the response has a status code of {int}")
    public void verifyResponseStatusCode(int code) {
        assertThat(response.getStatusCode(), equalTo(code));
    }

    @Then("the response body follows the {string} JSON schema")
    public void verifyResponseBodyAgainstJsonSchema(String type) {
        String filename = SCHEMAS_DIR + type.replaceAll(" ", "") + "Schema.json";
        assertThat(response.asString(), matchesJsonSchema(new File(filename)));
    }

    @Then("the results array contains {int} elements")
    public void verifyNumberOfResultsArrayElements(int numElements) {
        assertThat(JsonPath.from(response.asString()).getList("$").size(), equalTo(numElements));
    }

    @Then("the response body matches the {string} expected response")
    public void verifyResponseBodyAgainstExpectedResponse(String expectedResponse) {
        String filename = EXPECTED_RESPONSES_DIR + expectedResponse.replaceAll(" ", "") + "Response.json";
        Object expected = JsonPath.from(new File(filename)).get();
        assertThat(JsonPath.from(response.asString()).get(), equalTo(expected));
    }

    @Then("^the response body matches the (\\d+).{2} (?:post|comment|album|todo|user) in the \"(.*)\" expected response$")
    public void verifyResponseBodyAgainstPartOfExpectedResponse(int index, String expectedResponse) {
        String filename = EXPECTED_RESPONSES_DIR + expectedResponse.replaceAll(" ", "") + "Response.json";
        Object expected = JsonPath.from(new File(filename)).getList("$").get(index - 1);
        assertThat(JsonPath.from(response.asString()).get(), equalTo(expected));
    }

    @Then("the response body matches the following")
    public void verifyResponseBodyAgainstDataTable(DataTable dataTable) {
        Map<String, String> expectedBody = dataTable.subTable(1, 0).asMap(String.class, String.class);
        JsonPath actual = JsonPath.from(response.asString());
        assertThat(actual.getMap("$").keySet(), equalTo(expectedBody.keySet()));
        expectedBody.forEach((k, v) -> assertThat(actual.get(k).toString(), equalTo(expectedBody.get(k))));
    }

    @Then("^the \"(id|userId)\" field in the response body has a value of (\\d+)$")
    public void verifyResponseFieldValue(String field, int value) {
        assertThat(JsonPath.from(response.asString()).get(field), equalTo(value));
    }

    @Then("the {string} field in the response body has a value of {string}")
    public void verifyResponseFieldValue(String field, String value) {
        assertThat(JsonPath.from(response.asString()).get(field), equalTo(value));
    }

    @Then("the {string} field in the response body has a value of")
    public void verifyResponseFieldDocstring(String field, String value) {
        assertThat(JsonPath.from(response.asString()).get(field), equalTo(value));
    }

    @Then("the response body is an empty JSON object")
    public void verifyResponseBodyIsEmptyJSONObject() {
        assertThat(response.asString(), equalTo("{}"));
    }

    @Then("the two response bodies are identical")
    public void verifyResponseBodiesMatch() {
        String[] responseBodies = {responses.get(responses.size() - 2).asString(), responses.get(responses.size() - 1).asString()};
        for (int i = 0; i < responseBodies.length; i++) {
            if (responseBodies[i].startsWith("[")) {
                responseBodies[i] = JsonPath.from(responseBodies[i]).getList("$").get(0).toString();
            } else {
                responseBodies[i] = JsonPath.from(responseBodies[i]).get().toString();
            }
        }
        assertThat(responseBodies[1], equalTo(responseBodies[0]));
    }

    //This could be done with a library such as org.json to convert the map to a JSONObject
    //and then to a string but since it's a simple operation I have implemented it myself
    private String buildJsonString(Map<String, String> params) {
        StringBuilder requestBody = new StringBuilder("{");
        for (String key : new ArrayList<>(params.keySet())) {
            requestBody
                    .append("\"").append(key).append("\"")
                    .append(":")
                    .append("\"").append(params.get(key)).append("\"")
                    .append(",");
        }
        requestBody.deleteCharAt(requestBody.lastIndexOf(","));
        requestBody.append("}");
        return requestBody.toString();
    }
}
