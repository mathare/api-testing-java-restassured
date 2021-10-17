package com.typicode.jsonplaceholder.helpers;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestHelpers {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    public static Response sendGetRequestTo(String endpoint) {
        return RestAssured.get(BASE_URL + endpoint);
    }

    public static Response sendGetRequestTo(String endpoint, Map<String, String> params) {
        return RestAssured.given().queryParams(params).get(BASE_URL + endpoint);
    }

    public static Response sendPutRequestTo(String endpoint, String body) {
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .put(BASE_URL + endpoint);
    }

    public static Response sendPostRequestTo(String endpoint, String body) {
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .post(BASE_URL + endpoint);
    }

    public static Response sendDeleteRequestTo(String endpoint) {
        return RestAssured.delete(BASE_URL + endpoint);
    }

    //This could be done with a library such as org.json to convert the map to a JSONObject
    //and then to a string but since it's a simple operation I have implemented it myself
    public static String buildJsonString(Map<String, String> params) {
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
