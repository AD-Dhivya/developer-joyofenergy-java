package uk.tw.energy.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() throws Exception {
        // Optionally, seed data if needed
    }

    @Test
    void shouldGetPricePlanForSmartMeter() throws Exception {
        mockMvc.perform(get("/accounts/smart-meter-0/price-plan"))
            .andExpect(status().isOk())
            .andExpect(content().string("price-plan-0"));
    }

    @Test
    void shouldReturnNotFoundForUnknownSmartMeter() throws Exception {
        mockMvc.perform(get("/accounts/unknown-meter/price-plan"))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldListAllAccounts() throws Exception {
        mockMvc.perform(get("/accounts"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.smart-meter-0").value("price-plan-0"));
    }

    @Test
    void shouldUpdatePricePlanForSmartMeter() throws Exception {
        mockMvc.perform(put("/accounts/smart-meter-0/price-plan")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"pricePlanId\":\"new-plan-id\"}"))
            .andExpect(status().isOk());
        mockMvc.perform(get("/accounts/smart-meter-0/price-plan"))
            .andExpect(status().isOk())
            .andExpect(content().string("new-plan-id"));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingUnknownSmartMeter() throws Exception {
        mockMvc.perform(put("/accounts/unknown-meter/price-plan")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"pricePlanId\":\"plan-x\"}"))
            .andExpect(status().isNotFound());
    }
} 