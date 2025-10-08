package com.example.pricing;

import com.example.pricing.controller.PricingController;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;

@DirtiesContext
@AutoConfigureMessageVerifier
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class PricingApplicationTests {

    @Autowired
    private PricingController pricingController;

    @BeforeEach
    public void setup() {
        StandaloneMockMvcBuilder standaloneMockMvcBuilder
                = MockMvcBuilders.standaloneSetup(pricingController);
        RestAssuredMockMvc.standaloneSetup(standaloneMockMvcBuilder);
    }

}
