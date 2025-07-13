package com.springboot.tests;

import com.springboot.enums.BaseUrls;
import com.springboot.controller.ApiResponse;
import com.springboot.controller.ApplyOfferRequest;
import com.springboot.controller.ApplyOfferResponse;
import com.springboot.controller.OfferRequest;
import com.springboot.service.CartOfferApiService;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;

public class CartOfferTests {
    
    private CartOfferApiService apiService;

    @BeforeClass
    public void setUp() {
        apiService = new CartOfferApiService(BaseUrls.LOCAL_HOST, 9001);
    }

    // ==================== BASIC OFFER CREATION TESTS ====================

    @Test(priority = 1, groups = {"offer-creation", "smoke"}, description = "Test creating a FLATX offer for p1 customers")
    public void testCreateFlatXOfferForRegularCustomers() {
        OfferRequest offerRequest = new OfferRequest(
            3001,
            "FLATX",
            10,
            Arrays.asList("p1")
        );

        ApiResponse response = apiService.createOfferWithValidation(offerRequest);

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getResponse_msg(), "success");
    }

    @Test(priority = 1, groups = {"offer-creation", "smoke"}, description = "Test creating a PERCENTAGE offer for p2 customers")
    public void testCreatePercentageOfferForPremiumCustomers() {
        OfferRequest offerRequest = new OfferRequest(
            3002,
            "PERCENTAGE",
            15,
            Arrays.asList("p2")
        );

        ApiResponse response = apiService.createOfferAndValidateMessage(
            offerRequest, 
            "success"
        );

        Assert.assertNotNull(response);
    }

    @Test(priority = 1, groups = {"offer-creation"}, description = "Test creating offer for multiple customer segments")
    public void testCreateOfferForMultipleSegments() {
        OfferRequest offerRequest = new OfferRequest(
            3003,
            "FLATX",
            20,
            Arrays.asList("p1", "p2")
        );

        ApiResponse response = apiService.createOffer(offerRequest);

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getResponse_msg(), "success");
    }

    @Test(priority = 1, groups = {"offer-creation", "edge-cases"}, description = "Test creating offer with zero value")
    public void testCreateOfferWithZeroValue() {
        OfferRequest offerRequest = new OfferRequest(
            3004,
            "FLATX",
            0,
            Arrays.asList("p1")
        );

        ApiResponse response = apiService.createOfferWithValidation(offerRequest);

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getResponse_msg(), "success");
    }

    // ==================== BASIC OFFER APPLICATION TESTS ====================

    @Test(priority = 2, groups = {"cart-application", "smoke"}, description = "Test applying FLATX offer to cart")
    public void testApplyFlatXOfferToCart() {
        OfferRequest offerRequest = new OfferRequest(
            3001,
            "FLATX",
            10,
            Arrays.asList("p1")
        );
        apiService.createOfferWithValidation(offerRequest);

        ApplyOfferRequest applyRequest = new ApplyOfferRequest();
        applyRequest.setCart_value(100);
        applyRequest.setUser_id(1);
        applyRequest.setRestaurant_id(3001);

        ApplyOfferResponse response = apiService.applyOffer(applyRequest);

        Assert.assertNotNull(response);
        Assert.assertTrue(response.getCart_value() < 100, "Cart value should be reduced");
        Assert.assertTrue(response.getCart_value() >= 70, "Cart value should not be reduced too much");
    }

    @Test(priority = 2, groups = {"cart-application", "smoke"}, dependsOnMethods = "testCreatePercentageOfferForPremiumCustomers",
          description = "Test applying PERCENTAGE offer to cart")
    public void testApplyPercentageOfferToCart() {
        ApplyOfferRequest applyRequest = new ApplyOfferRequest();
        applyRequest.setCart_value(200);
        applyRequest.setUser_id(2);
        applyRequest.setRestaurant_id(3002);

        ApplyOfferResponse response = apiService.applyOffer(applyRequest);

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getCart_value(), 170);
    }

    @Test(priority = 2, groups = {"cart-application", "negative"}, description = "Test applying offer with no matching restaurant")
    public void testApplyOfferNoMatch() {
        ApplyOfferRequest applyRequest = new ApplyOfferRequest();
        applyRequest.setCart_value(100);
        applyRequest.setUser_id(1);
        applyRequest.setRestaurant_id(999);

        try {
            ApplyOfferResponse response = apiService.applyOffer(applyRequest);
            Assert.assertEquals(response.getCart_value(), 100);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("not found") || 
                            e.getMessage().contains("error"));
        }
    }

    // ==================== EDGE CASE TESTS ====================

    @Test(priority = 3, groups = {"edge-cases"}, description = "Test applying offer with zero cart value")
    public void testApplyOfferZeroCartValue() {
        OfferRequest offerRequest = new OfferRequest(
            3007,
            "FLATX",
            10,
            Arrays.asList("p1")
        );
        apiService.createOfferWithValidation(offerRequest);

        ApplyOfferRequest applyRequest = new ApplyOfferRequest();
        applyRequest.setCart_value(0);
        applyRequest.setUser_id(1);
        applyRequest.setRestaurant_id(3007);

        ApplyOfferResponse response = apiService.applyOffer(applyRequest);

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getCart_value(), -10);
    }

    @Test(priority = 3, groups = {"edge-cases"}, description = "Test applying offer greater than cart value")
    public void testApplyOfferGreaterThanCartValue() {
        OfferRequest offerRequest = new OfferRequest(
            3008,
            "FLATX",
            100,
            Arrays.asList("p1")
        );
        apiService.createOfferWithValidation(offerRequest);

        ApplyOfferRequest applyRequest = new ApplyOfferRequest();
        applyRequest.setCart_value(50);
        applyRequest.setUser_id(1);
        applyRequest.setRestaurant_id(3008);

        ApplyOfferResponse response = apiService.applyOffer(applyRequest);

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getCart_value(), -50);
    }

    @Test(priority = 3, groups = {"edge-cases"}, description = "Test 100% percentage offer")
    public void testApplyPercentageOffer100Percent() {
        OfferRequest offerRequest = new OfferRequest(
            3110,
            "PERCENTAGE",
            100,
            Arrays.asList("p1")
        );
        apiService.createOfferWithValidation(offerRequest);

        ApplyOfferRequest applyRequest = new ApplyOfferRequest();
        applyRequest.setCart_value(190);
        applyRequest.setUser_id(1);
        applyRequest.setRestaurant_id(3110);

        ApplyOfferResponse response = apiService.applyOffer(applyRequest);

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getCart_value(), 0);
    }

    // ==================== BUSINESS LOGIC TESTS ====================

    @Test(priority = 4, groups = {"business-logic"}, description = "Test multiple offers - first match wins")
    public void testMultipleOffersFirstMatchWins() {
        int restaurantId = 3011;

        OfferRequest firstOffer = new OfferRequest(
            restaurantId,
            "FLATX",
            20,
            Arrays.asList("p1")
        );
        apiService.createOfferWithValidation(firstOffer);

        OfferRequest secondOffer = new OfferRequest(
            restaurantId,
            "FLATX",
            30,
            Arrays.asList("p1")
        );
        apiService.createOfferWithValidation(secondOffer);

        ApplyOfferRequest applyRequest = new ApplyOfferRequest();
        applyRequest.setCart_value(200);
        applyRequest.setUser_id(1);
        applyRequest.setRestaurant_id(restaurantId);

        ApplyOfferResponse response = apiService.applyOffer(applyRequest);

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getCart_value(), 180);
    }

    @Test(priority = 4, groups = {"business-logic"}, description = "Test different restaurants same segment")
    public void testDifferentRestaurantsSameSegment() {
        OfferRequest offer1 = new OfferRequest(3012, "FLATX", 15, Arrays.asList("p1"));
        OfferRequest offer2 = new OfferRequest(3019, "FLATX", 15, Arrays.asList("p1"));
        
        apiService.createOfferWithValidation(offer1);
        apiService.createOfferWithValidation(offer2);

        ApplyOfferRequest applyRequest1 = new ApplyOfferRequest();
        applyRequest1.setCart_value(100);
        applyRequest1.setUser_id(1);
        applyRequest1.setRestaurant_id(3012);

        ApplyOfferResponse response1 = apiService.applyOffer(applyRequest1);
        Assert.assertEquals(response1.getCart_value(), 85);

        ApplyOfferRequest applyRequest2 = new ApplyOfferRequest();
        applyRequest2.setCart_value(100);
        applyRequest2.setUser_id(1);
        applyRequest2.setRestaurant_id(3019);

        ApplyOfferResponse response2 = apiService.applyOffer(applyRequest2);
        Assert.assertEquals(response2.getCart_value(), 85);
    }

    // ==================== INTEGRATION TESTS ====================

    @Test(priority = 5, groups = {"integration", "smoke"}, description = "Test end-to-end workflow")
    public void testEndToEndWorkflow() {
        OfferRequest offerRequest = new OfferRequest(
            3014,
            "FLATX",
            200,
            Arrays.asList("p1")
        );

        ApiResponse createResponse = apiService.createOfferWithValidation(offerRequest);
        Assert.assertEquals(createResponse.getResponse_msg(), "success");

        ApplyOfferRequest applyRequest = new ApplyOfferRequest();
        applyRequest.setCart_value(500);
        applyRequest.setUser_id(1);
        applyRequest.setRestaurant_id(3014);

        ApplyOfferResponse applyResponse = apiService.applyOffer(applyRequest);
        Assert.assertNotNull(applyResponse);
        Assert.assertEquals(applyResponse.getCart_value(), 300);
    }

    // ==================== ADDITIONAL CRITICAL TESTS ====================

    @Test(priority = 3, groups = {"edge-cases"}, description = "Test applying offer with negative cart value")
    public void testApplyOfferNegativeCartValue() {
        OfferRequest offerRequest = new OfferRequest(
            3015,
            "FLATX",
            25,
            Arrays.asList("p1")
        );
        apiService.createOfferWithValidation(offerRequest);

        ApplyOfferRequest applyRequest = new ApplyOfferRequest();
        applyRequest.setCart_value(-100);
        applyRequest.setUser_id(1);
        applyRequest.setRestaurant_id(3015);

        ApplyOfferResponse response = apiService.applyOffer(applyRequest);

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getCart_value(), -125);
    }

    @Test(priority = 3, groups = {"edge-cases"}, description = "Test applying offer with high cart value")
    public void testApplyOfferWithHighCartValue() {
        OfferRequest offerRequest = new OfferRequest(
            3016,
            "PERCENTAGE",
            5,
            Arrays.asList("p1")
        );
        apiService.createOfferWithValidation(offerRequest);

        ApplyOfferRequest applyRequest = new ApplyOfferRequest();
        applyRequest.setCart_value(10000);
        applyRequest.setUser_id(1);
        applyRequest.setRestaurant_id(3016);

        ApplyOfferResponse response = apiService.applyOffer(applyRequest);

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getCart_value(), 9500);
    }

}
