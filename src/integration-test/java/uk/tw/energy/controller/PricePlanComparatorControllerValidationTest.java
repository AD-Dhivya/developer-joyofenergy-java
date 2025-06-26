package uk.tw.energy.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PricePlanComparatorControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturn400WhenSmartMeterIdNotFound_compareAll() throws Exception {
        mockMvc.perform(get("/price-plans/compare-all/not-found"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value("No valid readings found for smartMeterId: not-found"));
    }

    @Test
    void shouldReturn400WhenSmartMeterIdNotFound_recommend() throws Exception {
        mockMvc.perform(get("/price-plans/recommend/not-found"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value("No valid readings found for smartMeterId: not-found"));
    }

    @Test
    void shouldReturn400WhenLimitIsInvalid() throws Exception {
        mockMvc.perform(get("/price-plans/recommend/some-meter-id?limit=-1"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value("No valid readings found for smartMeterId: some-meter-id"));
    }
}