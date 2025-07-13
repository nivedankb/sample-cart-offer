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
import java.util.Collections;

public class CartOfferSegmentTests {
    
    private CartOfferApiService apiService;

    @BeforeClass
    public void setUp() {
        apiService = new CartOfferApiService(BaseUrls.LOCAL_HOST, 9001);
    }

    // ==================== USER SEGMENT SPECIFIC TESTS ====================

    @Test(priority = 1, groups = {"segment", "smoke"}, description = "Test applying offer for user segment p1 with matching offer")
    public void testApplyOffer_UserSegmentP1_MatchingOffer() {
        OfferRequest offerRequest = new OfferRequest(1, "FLATX", 15, Collections.singletonList("p1"));
        ApiResponse createResponse = apiService.createOfferWithValidation(offerRequest);
        Assert.assertEquals(createResponse.getResponse_msg(), "success");

        ApplyOfferRequest applyRequest = new ApplyOfferRequest();
        applyRequest.setCart_value(100);
        applyRequest.setUser_id(1);
        applyRequest.setRestaurant_id(1);

        ApplyOfferResponse response = apiService.applyOfferAndValidateCartValue(applyRequest, 85);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getCart_value(), 85);
    }

    @Test(priority = 1, groups = {"segment", "negative"}, description = "Test applying offer for user segment p1 with no matching segment offer")
    public void testApplyOffer_UserSegmentP1_NoMatchingSegmentOffer() {
        OfferRequest offerRequest = new OfferRequest(2, "FLATX", 20, Collections.singletonList("p2"));
        ApiResponse createResponse = apiService.createOfferWithValidation(offerRequest);
        Assert.assertEquals(createResponse.getResponse_msg(), "success");

        ApplyOfferRequest applyRequest = new ApplyOfferRequest();
        applyRequest.setCart_value(100);
        applyRequest.setUser_id(1);
        applyRequest.setRestaurant_id(2);

        ApplyOfferResponse response = apiService.applyOffer(applyRequest);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getCart_value(), 100);
    }

    @Test(priority = 1, groups = {"segment"}, description = "Test applying offer for multiple segment offer with user p1")
    public void testApplyOffer_MultipleSegmentOffer_UserP1() {
        OfferRequest offerRequest = new OfferRequest(3, "FLAT_X", 25, 
                Arrays.asList("p1", "p2", "p3"));
        ApiResponse createResponse = apiService.createOfferWithValidation(offerRequest);
        Assert.assertEquals(createResponse.getResponse_msg(), "success");

        ApplyOfferRequest applyRequest = new ApplyOfferRequest();
        applyRequest.setCart_value(200);
        applyRequest.setUser_id(1);
        applyRequest.setRestaurant_id(3);

        ApplyOfferResponse response = apiService.applyOfferAndValidateCartValue(applyRequest, 150);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getCart_value(), 150);
    }

    @Test(priority = 2, groups = {"segment", "premium"}, description = "Test applying offer for premium user segment p2")
    public void testApplyOffer_UserSegmentP2_PremiumUser() {
        OfferRequest offerRequest = new OfferRequest(4, "PERCENTAGE", 20, Collections.singletonList("p2"));
        ApiResponse createResponse = apiService.createOfferWithValidation(offerRequest);
        Assert.assertEquals(createResponse.getResponse_msg(), "success");

        ApplyOfferRequest applyRequest = new ApplyOfferRequest();
        applyRequest.setCart_value(500);
        applyRequest.setUser_id(2);
        applyRequest.setRestaurant_id(4);

        ApplyOfferResponse response = apiService.applyOffer(applyRequest);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getCart_value(), 400);
    }

    @Test(priority = 2, groups = {"segment", "vip"}, description = "Test applying offer for VIP user segment p3")
    public void testApplyOffer_UserSegmentP3_VipUser() {
        OfferRequest offerRequest = new OfferRequest(5, "FLAT_X", 50, Collections.singletonList("p3"));
        ApiResponse createResponse = apiService.createOfferWithValidation(offerRequest);
        Assert.assertEquals(createResponse.getResponse_msg(), "success");

        ApplyOfferRequest applyRequest = new ApplyOfferRequest();
        applyRequest.setCart_value(300);
        applyRequest.setUser_id(3);
        applyRequest.setRestaurant_id(5);

        ApplyOfferResponse response = apiService.applyOfferAndValidateCartValue(applyRequest, 150);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getCart_value(), 150);
    }

    // ==================== EDGE CASE TESTS ====================

    @Test(priority = 3, groups = {"segment", "edge-cases"}, description = "Test applying offer with invalid user ID")
    public void testApplyOffer_InvalidUserId() {
        OfferRequest offerRequest = new OfferRequest(6, "FLAT_X", 10, Collections.singletonList("p1"));
        ApiResponse createResponse = apiService.createOfferWithValidation(offerRequest);
        Assert.assertEquals(createResponse.getResponse_msg(), "success");

        ApplyOfferRequest applyRequest = new ApplyOfferRequest();
        applyRequest.setCart_value(100);
        applyRequest.setUser_id(999);
        applyRequest.setRestaurant_id(6);

        ApplyOfferResponse response = apiService.applyOffer(applyRequest);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getCart_value(), 100);
    }

    @Test(priority = 3, groups = {"segment", "edge-cases"}, description = "Test applying offer with negative cart value")
    public void testApplyOffer_NegativeCartValue() {
        OfferRequest offerRequest = new OfferRequest(7, "FLAT_X", 10, Collections.singletonList("p1"));
        ApiResponse createResponse = apiService.createOfferWithValidation(offerRequest);
        Assert.assertEquals(createResponse.getResponse_msg(), "success");

        ApplyOfferRequest applyRequest = new ApplyOfferRequest();
        applyRequest.setCart_value(-50);
        applyRequest.setUser_id(1);
        applyRequest.setRestaurant_id(7);

        ApplyOfferResponse response = apiService.applyOffer(applyRequest);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getCart_value(), -45);
    }

    @Test(priority = 3, groups = {"segment", "edge-cases"}, description = "Test applying offer with zero offer value")
    public void testApplyOffer_ZeroOfferValue() {
        OfferRequest offerRequest = new OfferRequest(8, "FLAT_X", 0, Collections.singletonList("p1"));
        ApiResponse createResponse = apiService.createOfferWithValidation(offerRequest);
        Assert.assertEquals(createResponse.getResponse_msg(), "success");

        ApplyOfferRequest applyRequest = new ApplyOfferRequest();
        applyRequest.setCart_value(100);
        applyRequest.setUser_id(1);
        applyRequest.setRestaurant_id(8);

        ApplyOfferResponse response = apiService.applyOfferAndValidateCartValue(applyRequest, 100);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getCart_value(), 100);
    }

    @Test(priority = 3, groups = {"segment", "edge-cases"}, description = "Test 100% percentage offer edge case")
    public void testApplyOffer_PercentageOfferEdgeCase() {
        OfferRequest offerRequest = new OfferRequest(9, "PERCENTAGE", 100, Collections.singletonList("p1"));
        ApiResponse createResponse = apiService.createOfferWithValidation(offerRequest);
        Assert.assertEquals(createResponse.getResponse_msg(), "success");

        ApplyOfferRequest applyRequest = new ApplyOfferRequest();
        applyRequest.setCart_value(200);
        applyRequest.setUser_id(1);
        applyRequest.setRestaurant_id(9);

        ApplyOfferResponse response = apiService.applyOfferAndValidateCartValue(applyRequest, 0);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getCart_value(), 0);
    }

    // ==================== BUSINESS LOGIC TESTS ====================

    @Test(priority = 4, groups = {"segment", "business-logic"}, description = "Test different restaurants with same segment")
    public void testApplyOffer_DifferentRestaurants_SameSegment() {
        OfferRequest offer1 = new OfferRequest(10, "FLATX", 10, Collections.singletonList("p1"));
        OfferRequest offer2 = new OfferRequest(11, "FLATX", 20, Collections.singletonList("p1"));
        
        ApiResponse createResponse1 = apiService.createOfferWithValidation(offer1);
        ApiResponse createResponse2 = apiService.createOfferWithValidation(offer2);
        
        Assert.assertEquals(createResponse1.getResponse_msg(), "success");
        Assert.assertEquals(createResponse2.getResponse_msg(), "success");

        ApplyOfferRequest applyRequest1 = new ApplyOfferRequest();
        applyRequest1.setCart_value(100);
        applyRequest1.setUser_id(1);
        applyRequest1.setRestaurant_id(10);

        ApplyOfferResponse response1 = apiService.applyOffer(applyRequest1);
        Assert.assertEquals(response1.getCart_value(), 90);

        ApplyOfferRequest applyRequest2 = new ApplyOfferRequest();
        applyRequest2.setCart_value(100);
        applyRequest2.setUser_id(1);
        applyRequest2.setRestaurant_id(11);

        ApplyOfferResponse response2 = apiService.applyOffer(applyRequest2);
        Assert.assertEquals(response2.getCart_value(), 80);
    }

    @Test(priority = 4, groups = {"segment", "business-logic"}, description = "Test same restaurant with different offer types - first match wins")
    public void testApplyOffer_SameRestaurant_DifferentOfferTypes() {
        OfferRequest flatOffer = new OfferRequest(12, "FLAT_X", 15, Collections.singletonList("p1"));
        ApiResponse flatResponse = apiService.createOfferWithValidation(flatOffer);
        Assert.assertEquals(flatResponse.getResponse_msg(), "success");

        OfferRequest percentOffer = new OfferRequest(12, "PERCENTAGE", 10, Collections.singletonList("p1"));
        ApiResponse percentResponse = apiService.createOfferWithValidation(percentOffer);
        Assert.assertEquals(percentResponse.getResponse_msg(), "success");

        ApplyOfferRequest applyRequest = new ApplyOfferRequest();
        applyRequest.setCart_value(200);
        applyRequest.setUser_id(1);
        applyRequest.setRestaurant_id(12);

        ApplyOfferResponse response = apiService.applyOffer(applyRequest);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getCart_value(), 170);
    }

    @Test(priority = 4, groups = {"segment", "business-logic"}, description = "Test cross-segment offer application")
    public void testApplyOffer_CrossSegmentOfferApplication() {
        OfferRequest offerRequest = new OfferRequest(13, "PERCENTAGE", 15, Arrays.asList("p1", "p3"));
        ApiResponse createResponse = apiService.createOfferWithValidation(offerRequest);
        Assert.assertEquals(createResponse.getResponse_msg(), "success");

        ApplyOfferRequest applyRequestP1 = new ApplyOfferRequest();
        applyRequestP1.setCart_value(100);
        applyRequestP1.setUser_id(1);
        applyRequestP1.setRestaurant_id(13);

        ApplyOfferResponse responseP1 = apiService.applyOffer(applyRequestP1);
        Assert.assertEquals(responseP1.getCart_value(), 85);

        ApplyOfferRequest applyRequestP3 = new ApplyOfferRequest();
        applyRequestP3.setCart_value(100);
        applyRequestP3.setUser_id(3);
        applyRequestP3.setRestaurant_id(13);

        ApplyOfferResponse responseP3 = apiService.applyOffer(applyRequestP3);
        Assert.assertEquals(responseP3.getCart_value(), 85);

        ApplyOfferRequest applyRequestP2 = new ApplyOfferRequest();
        applyRequestP2.setCart_value(100);
        applyRequestP2.setUser_id(2);
        applyRequestP2.setRestaurant_id(13);

        ApplyOfferResponse responseP2 = apiService.applyOffer(applyRequestP2);
        Assert.assertEquals(responseP2.getCart_value(), 100);
    }

    // ==================== INTEGRATION TESTS ====================

    @Test(priority = 5, groups = {"segment", "integration"}, description = "Test end-to-end segment-based offer workflow")
    public void testSegmentBasedOfferWorkflow() {
        OfferRequest regularOffer = new OfferRequest(14, "FLAT_X", 10, Collections.singletonList("p1"));
        OfferRequest premiumOffer = new OfferRequest(15, "PERCENTAGE", 20, Collections.singletonList("p2"));
        OfferRequest vipOffer = new OfferRequest(16, "FLATX", 50, Collections.singletonList("p3"));

        ApiResponse regularResponse = apiService.createOfferWithValidation(regularOffer);
        ApiResponse premiumResponse = apiService.createOfferWithValidation(premiumOffer);
        ApiResponse vipResponse = apiService.createOfferWithValidation(vipOffer);

        Assert.assertEquals(regularResponse.getResponse_msg(), "success");
        Assert.assertEquals(premiumResponse.getResponse_msg(), "success");
        Assert.assertEquals(vipResponse.getResponse_msg(), "success");

        ApplyOfferRequest regularApply = new ApplyOfferRequest();
        regularApply.setCart_value(100);
        regularApply.setUser_id(1);
        regularApply.setRestaurant_id(14);

        ApplyOfferResponse regularResult = apiService.applyOfferAndValidateCartValue(regularApply, 90);
        Assert.assertEquals(regularResult.getCart_value(), 90);

        ApplyOfferRequest premiumApply = new ApplyOfferRequest();
        premiumApply.setCart_value(100);
        premiumApply.setUser_id(2);
        premiumApply.setRestaurant_id(15);

        ApplyOfferResponse premiumResult = apiService.applyOfferAndValidateCartValue(premiumApply, 80);
        Assert.assertEquals(premiumResult.getCart_value(), 80);

        ApplyOfferRequest vipApply = new ApplyOfferRequest();
        vipApply.setCart_value(100);
        vipApply.setUser_id(3);
        vipApply.setRestaurant_id(16);

        ApplyOfferResponse vipResult = apiService.applyOfferAndValidateCartValue(vipApply, 50);
        Assert.assertEquals(vipResult.getCart_value(), 50);
    }

    @Test(priority = 5, groups = {"segment", "integration"}, description = "Test segment isolation - users don't get other segment offers")
    public void testSegmentIsolation() {
        OfferRequest p2Offer = new OfferRequest(17, "FLAT_X", 30, Collections.singletonList("p2"));
        ApiResponse createResponse = apiService.createOfferWithValidation(p2Offer);
        Assert.assertEquals(createResponse.getResponse_msg(), "success");

        ApplyOfferRequest p1Apply = new ApplyOfferRequest();
        p1Apply.setCart_value(100);
        p1Apply.setUser_id(1);
        p1Apply.setRestaurant_id(17);

        ApplyOfferResponse p1Response = apiService.applyOffer(p1Apply);
        Assert.assertEquals(p1Response.getCart_value(), 100);

        ApplyOfferRequest p3Apply = new ApplyOfferRequest();
        p3Apply.setCart_value(100);
        p3Apply.setUser_id(3);
        p3Apply.setRestaurant_id(17);

        ApplyOfferResponse p3Response = apiService.applyOffer(p3Apply);
        Assert.assertEquals(p3Response.getCart_value(), 100);

        ApplyOfferRequest p2Apply = new ApplyOfferRequest();
        p2Apply.setCart_value(100);
        p2Apply.setUser_id(2);
        p2Apply.setRestaurant_id(17);

        ApplyOfferResponse p2Response = apiService.applyOfferAndValidateCartValue(p2Apply, 70);
        Assert.assertEquals(p2Response.getCart_value(), 70);
    }

}
