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

    public static String buildQueryParamsString(Map<String, String> params) {
        StringBuilder paramString = new StringBuilder();
        List<String> keys = new ArrayList<>(params.keySet());
        for (String key : keys) {
            paramString.append("&").append(key).append("=").append(params.get(key));
        }
        //Strip off initial &
        return "?" + paramString.substring(1);
    }
}
