package com.springboot.enums;

public enum ApiEndpoints {
    CREATE_OFFER("/api/v1/offer"),
    APPLY_OFFER("/api/v1/cart/apply_offer"),
    GET_USER_SEGMENT("/api/v1/user/segment"); // For future use

    private final String endpoint;

    ApiEndpoints(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }

    @Override
    public String toString() {
        return endpoint;
    }
}
