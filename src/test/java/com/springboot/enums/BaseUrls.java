package com.springboot.enums;

public enum BaseUrls {
    LOCAL_HOST("http://localhost");

    private final String baseUrl;

    BaseUrls(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getBaseUrlWithPort(int port) {
        if (this == LOCAL_HOST) {
            return baseUrl + ":" + port;
        }
        return baseUrl;
    }

    @Override
    public String toString() {
        return baseUrl;
    }
}
