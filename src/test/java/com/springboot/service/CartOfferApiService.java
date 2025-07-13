package com.springboot.service;

import com.springboot.client.RestClient;
import com.springboot.enums.ApiEndpoints;
import com.springboot.enums.BaseUrls;
import com.springboot.controller.ApiResponse;
import com.springboot.controller.ApplyOfferRequest;
import com.springboot.controller.ApplyOfferResponse;
import com.springboot.controller.OfferRequest;
import io.restassured.response.Response;

public class CartOfferApiService {
    
    public CartOfferApiService(BaseUrls baseUrl, int port) {
        RestClient.configure(baseUrl, port);
    }

    public ApiResponse createOffer(OfferRequest offerRequest) {
        Response response = RestClient.postRequest(ApiEndpoints.CREATE_OFFER, offerRequest);
        return response.as(ApiResponse.class);
    }

    public ApplyOfferResponse applyOffer(ApplyOfferRequest applyOfferRequest) {
        Response response = RestClient.postRequest(ApiEndpoints.APPLY_OFFER, applyOfferRequest);
        return response.as(ApplyOfferResponse.class);
    }

    public ApiResponse createOfferWithValidation(OfferRequest offerRequest) {
        Response response = RestClient.postRequest(ApiEndpoints.CREATE_OFFER, offerRequest);
        
        // Validate status code
        RestClient.validateStatusCode(response, 200);
        
        return response.as(ApiResponse.class);
    }

    public ApplyOfferResponse applyOfferWithValidation(ApplyOfferRequest applyOfferRequest) {
        Response response = RestClient.postRequest(ApiEndpoints.APPLY_OFFER, applyOfferRequest);
        
        // Validate status code
        RestClient.validateStatusCode(response, 200);
        
        return response.as(ApplyOfferResponse.class);
    }

    public ApiResponse createOfferAndValidateMessage(OfferRequest offerRequest, String expectedMessage) {
        ApiResponse apiResponse = createOfferWithValidation(offerRequest);
        
        if (!expectedMessage.equals(apiResponse.getResponse_msg())) {
            throw new AssertionError("Expected response message: " + expectedMessage + 
                                   ", but got: " + apiResponse.getResponse_msg());
        }
        
        return apiResponse;
    }

    public ApplyOfferResponse applyOfferAndValidateCartValue(ApplyOfferRequest applyOfferRequest, int expectedCartValue) {
        ApplyOfferResponse applyOfferResponse = applyOfferWithValidation(applyOfferRequest);
        
        if (expectedCartValue != applyOfferResponse.getCart_value()) {
            throw new AssertionError("Expected cart value: " + expectedCartValue + 
                                   ", but got: " + applyOfferResponse.getCart_value());
        }
        
        return applyOfferResponse;
    }
}
