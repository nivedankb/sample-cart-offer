package com.springboot.client;

import com.springboot.enums.ApiEndpoints;
import com.springboot.enums.BaseUrls;
import com.springboot.enums.RequestType;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Reporter;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.fail;

public class RestClient {

    private static BaseUrls currentBaseUrl = BaseUrls.LOCAL_HOST;
    private static int currentPort = 8080;

    public static void configure(BaseUrls baseUrl, int port) {
        currentBaseUrl = baseUrl;
        currentPort = port;
        
        if (baseUrl == BaseUrls.LOCAL_HOST) {
            RestAssured.baseURI = baseUrl.getBaseUrl();
            RestAssured.port = port;
        } else {
            RestAssured.baseURI = baseUrl.getBaseUrl();
            RestAssured.port = -1; // Use default port
        }
        
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        Reporter.log("REST Client configured with base URL: " + getFullBaseUrl(), true);
    }

    public static String getFullBaseUrl() {
        return currentBaseUrl.getBaseUrlWithPort(currentPort);
    }

    public static Response getResponse(
            final RequestType requestType, 
            final RequestSpecification reqSpecification) {
        
        RestAssured.defaultParser = Parser.JSON;
        
        try {
            switch (requestType) {
                case GET:
                    return given()
                        .spec(reqSpecification)
                        .when()
                        .log().all()
                        .get()
                        .then()
                        .extract()
                        .response();
                        
                case POST:
                    return given()
                        .spec(reqSpecification)
                        .when()
                        .log().all()
                        .post()
                        .then()
                        .extract()
                        .response();
                        
                case PUT:
                    return given()
                        .spec(reqSpecification)
                        .when()
                        .log().all()
                        .put()
                        .then()
                        .extract()
                        .response();
                        
                case PATCH:
                    return given()
                        .spec(reqSpecification)
                        .when()
                        .log().all()
                        .patch()
                        .then()
                        .extract()
                        .response();
                        
                case DELETE:
                    return given()
                        .spec(reqSpecification)
                        .when()
                        .log().all()
                        .delete()
                        .then()
                        .extract()
                        .response();
                        
                default:
                    throw new IllegalArgumentException("Unsupported request type: " + requestType);
            }
        } catch (Exception e) {
            Reporter.log("Exception Occurred : " + e.getMessage(), true);
            fail("API call failure: " + e.getMessage());
            return null;
        }
    }

    public static Response postRequest(ApiEndpoints endpoint, Object requestBody) {
        RequestSpecification reqSpec = given()
            .contentType("application/json")
            .body(requestBody)
            .basePath(endpoint.getEndpoint());
            
        return getResponse(RequestType.POST, reqSpec);
    }

    public static void validateStatusCode(Response response, int expectedStatusCode) {
        int actualStatusCode = response.getStatusCode();
        if (actualStatusCode != expectedStatusCode) {
            Reporter.log("Status code mismatch. Expected: " + expectedStatusCode + 
                        ", Actual: " + actualStatusCode, true);
            Reporter.log("Response body: " + response.getBody().asString(), true);
            fail("Status code validation failed");
        }
    }
}
