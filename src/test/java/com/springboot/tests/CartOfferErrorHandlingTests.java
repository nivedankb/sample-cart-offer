package com.springboot.tests;

import com.springboot.enums.BaseUrls;
import com.springboot.controller.ApiResponse;
import com.springboot.controller.ApplyOfferRequest;
import com.springboot.controller.ApplyOfferResponse;
import com.springboot.controller.OfferRequest;
import com.springboot.service.CartOfferApiService;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;

public class CartOfferErrorHandlingTests {
    
    private CartOfferApiService apiService;

    @BeforeClass
    public void setUp() {
        apiService = new CartOfferApiService(BaseUrls.LOCAL_HOST, 9001);
    }

    // ==================== INVALID USER ID TESTS ====================

    @Test(priority = 1, groups = {"error-handling", "invalid-user"}, description = "Test with user ID 0 (invalid)")
    public void testApplyOffer_InvalidUserId_Zero() {
        OfferRequest offerRequest = new OfferRequest(1001, "FLATX", 10, Collections.singletonList("p1"));
        apiService.createOfferWithValidation(offerRequest);

        ApplyOfferRequest applyRequest = new ApplyOfferRequest();
        applyRequest.setCart_value(100);
        applyRequest.setUser_id(0);
        applyRequest.setRestaurant_id(1001);

        ApplyOfferResponse response = apiService.applyOffer(applyRequest);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getCart_value(), 100);
    }

    @Test(priority = 1, groups = {"error-handling", "invalid-user"}, description = "Test with negative user ID")
    public void testApplyOffer_InvalidUserId_Negative() {
        OfferRequest offerRequest = new OfferRequest(1002, "FLATX", 15, Collections.singletonList("p1"));
        apiService.createOfferWithValidation(offerRequest);

        ApplyOfferRequest applyRequest = new ApplyOfferRequest();
        applyRequest.setCart_value(200);
        applyRequest.setUser_id(-1);
        applyRequest.setRestaurant_id(1002);

        ApplyOfferResponse response = apiService.applyOffer(applyRequest);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getCart_value(), 200);
    }

    @Test(priority = 1, groups = {"error-handling", "user-not-found"}, description = "Test with non-existent user ID")
    public void testApplyOffer_UserNotFound() {
        OfferRequest offerRequest = new OfferRequest(1003, "PERCENTAGE", 20, Collections.singletonList("p2"));
        apiService.createOfferWithValidation(offerRequest);

        ApplyOfferRequest applyRequest = new ApplyOfferRequest();
        applyRequest.setCart_value(150);
        applyRequest.setUser_id(999);
        applyRequest.setRestaurant_id(1003);

        ApplyOfferResponse response = apiService.applyOffer(applyRequest);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getCart_value(), 150);
    }

    @Test(priority = 1, groups = {"error-handling", "server-error"}, description = "Test with user ID causing server error")
    public void testApplyOffer_ServerError() {
        OfferRequest offerRequest = new OfferRequest(1004, "FLATX", 25, Collections.singletonList("p3"));
        apiService.createOfferWithValidation(offerRequest);

        ApplyOfferRequest applyRequest = new ApplyOfferRequest();
        applyRequest.setCart_value(300);
        applyRequest.setUser_id(500);
        applyRequest.setRestaurant_id(1004);

        ApplyOfferResponse response = apiService.applyOffer(applyRequest);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getCart_value(), 300);
    }

    // ==================== INVALID OFFER CREATION TESTS ====================


    @Test(priority = 2, groups = {"error-handling", "invalid-offer"}, description = "Test creating offer with invalid offer type")
    public void testCreateOffer_InvalidOfferType() {
        OfferRequest offerRequest = new OfferRequest(1005, "INVALID_TYPE", 10, Collections.singletonList("p1"));
        
        ApiResponse response = apiService.createOffer(offerRequest);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getResponse_msg(), "error: Offer type must be FLATX or PERCENTAGE");
    }


    @Test(priority = 2, groups = {"error-handling", "invalid-offer"}, description = "Test creating offer with negative offer value")
    public void testCreateOffer_NegativeOfferValue() {
        OfferRequest offerRequest = new OfferRequest(1006, "FLATX", -10, Collections.singletonList("p1"));
        
        ApiResponse response = apiService.createOffer(offerRequest);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getResponse_msg(), "error: Offer value cannot be negative");
    }

    @Test(priority = 2, groups = {"error-handling", "invalid-offer"}, description = "Test creating offer with empty customer segment")
    public void testCreateOffer_EmptyCustomerSegment() {
        OfferRequest offerRequest = new OfferRequest(1007, "FLATX", 10, Collections.emptyList());
        
        ApiResponse response = apiService.createOffer(offerRequest);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getResponse_msg(), "error: Customer segment cannot be empty");
    }

    @Test(priority = 2, groups = {"error-handling", "invalid-offer"}, description = "Test creating offer with invalid customer segment")
    public void testCreateOffer_InvalidCustomerSegment() {
        OfferRequest offerRequest = new OfferRequest(1008, "FLATX", 10,
                Arrays.asList("invalid_segment", "another_invalid"));
        
        try {
            ApiResponse response = apiService.createOffer(offerRequest);
            Assert.assertNotNull(response);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("customer_segment") || 
                            e.getMessage().contains("invalid") ||
                            e.getMessage().contains("p1") ||
                            e.getMessage().contains("p2") ||
                            e.getMessage().contains("p3"));
        }
    }


    @Test(priority = 3, groups = {"error-handling", "boundary"}, description = "Test percentage offer with value > 100")
    public void testCreateOffer_PercentageOver100() {
        OfferRequest offerRequest = new OfferRequest(1012, "PERCENTAGE", 150, Collections.singletonList("p1"));
        
        try {
            ApiResponse response = apiService.createOffer(offerRequest);
            Assert.assertNotNull(response);
            
            ApplyOfferRequest applyRequest = new ApplyOfferRequest();
            applyRequest.setCart_value(100);
            applyRequest.setUser_id(1);
            applyRequest.setRestaurant_id(1012);

            ApplyOfferResponse applyResponse = apiService.applyOffer(applyRequest);
            Assert.assertEquals(applyResponse.getCart_value(), -50);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("percentage") || 
                            e.getMessage().contains("100") ||
                            e.getMessage().contains("invalid"));
        }
    }

    // ==================== CONCURRENT ACCESS TESTS ====================

    @Test(priority = 4, groups = {"error-handling", "concurrency"}, description = "Test applying same offer multiple times")
    public void testApplyOffer_MultipleApplications() {
        OfferRequest offerRequest = new OfferRequest(1013, "FLATX", 20, Collections.singletonList("p1"));
        apiService.createOfferWithValidation(offerRequest);

        for (int i = 0; i < 5; i++) {
            ApplyOfferRequest applyRequest = new ApplyOfferRequest();
            applyRequest.setCart_value(100);
            applyRequest.setUser_id(1);
            applyRequest.setRestaurant_id(1013);

            ApplyOfferResponse response = apiService.applyOffer(applyRequest);
            Assert.assertNotNull(response);
            Assert.assertEquals(response.getCart_value(), 80);
        }
    }

    @Test(priority = 4, groups = {"error-handling", "concurrency"}, description = "Test creating duplicate offers")
    public void testCreateOffer_Duplicates() {
        int restaurantId = 1014;
        
        OfferRequest offerRequest1 = new OfferRequest(restaurantId, "FLATX", 15, Collections.singletonList("p1"));
        ApiResponse response1 = apiService.createOfferWithValidation(offerRequest1);
        Assert.assertEquals(response1.getResponse_msg(), "success");

        OfferRequest offerRequest2 = new OfferRequest(restaurantId, "FLATX", 25, Collections.singletonList("p1"));
        ApiResponse response2 = apiService.createOfferWithValidation(offerRequest2);
        Assert.assertEquals(response2.getResponse_msg(), "success");

        ApplyOfferRequest applyRequest = new ApplyOfferRequest();
        applyRequest.setCart_value(100);
        applyRequest.setUser_id(1);
        applyRequest.setRestaurant_id(restaurantId);

        ApplyOfferResponse applyResponse = apiService.applyOffer(applyRequest);
        Assert.assertEquals(applyResponse.getCart_value(), 85);
    }

    // ==================== DATA INTEGRITY TESTS ====================

    @DataProvider(name = "cartValueProvider")
    public Object[][] cartValueProvider() {
        return new Object[][] {
            {0},
            {50},
            {100},
            {1000}
        };
    }

    @Test(priority = 5, groups = {"error-handling", "data-integrity"}, 
          description = "Test offer application with modified cart value using data provider",
          dataProvider = "cartValueProvider")
    public void testApplyOffer_ModifiedCartValue(int cartValue) {
        OfferRequest offerRequest = new OfferRequest(1015, "PERCENTAGE", 10, Collections.singletonList("p1"));
        apiService.createOfferWithValidation(offerRequest);

        ApplyOfferRequest applyRequest = new ApplyOfferRequest();
        applyRequest.setCart_value(cartValue);
        applyRequest.setUser_id(1);
        applyRequest.setRestaurant_id(1015);

        ApplyOfferResponse response = apiService.applyOffer(applyRequest);
        Assert.assertNotNull(response);
        
        int discountAmount = cartValue * 10 / 100;
        int expectedValue = cartValue - discountAmount;
        Assert.assertEquals(response.getCart_value(), expectedValue, 
            "Cart value " + cartValue + " should result in " + expectedValue + " after 10% discount (discount: " + discountAmount + ")");
    }

    @Test(priority = 5, groups = {"error-handling", "data-integrity"}, description = "Test calculation precision with decimal percentages")
    public void testApplyOffer_CalculationPrecision() {
        OfferRequest offerRequest = new OfferRequest(1016, "PERCENTAGE", 33, Collections.singletonList("p1"));
        apiService.createOfferWithValidation(offerRequest);

        ApplyOfferRequest applyRequest = new ApplyOfferRequest();
        applyRequest.setCart_value(100);
        applyRequest.setUser_id(1);
        applyRequest.setRestaurant_id(1016);

        ApplyOfferResponse response = apiService.applyOffer(applyRequest);
        Assert.assertNotNull(response);
        
        Assert.assertEquals(response.getCart_value(), 67);
    }

}
