package com.typicode.jsonplaceholder.steps;

import io.cucumber.java.en.Then;
import io.restassured.path.json.JsonPath;

import static com.typicode.jsonplaceholder.steps.CommonSteps.response;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PostsSteps {

    @Then("^the \"(id|userId)\" field in the response body has a value of (\\d+)$")
    public static void verifyResponseFieldValue(String field, int value) {
        assertThat(JsonPath.from(response.asString()).get(field), equalTo(value));
    }

    @Then("the {string} field in the response body has a value of {string}")
    public static void verifyResponseFieldValue(String field, String value) {
        assertThat(JsonPath.from(response.asString()).get(field), equalTo(value));
    }

    @Then("the {string} field in the response body has a value of")
    public static void verifyResponseFieldDocstring(String field, String value) {
        assertThat(JsonPath.from(response.asString()).get(field), equalTo(value));
    }
}
