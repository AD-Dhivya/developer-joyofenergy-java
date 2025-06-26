package uk.tw.energy.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MeterReadingControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldStoreReadingsSuccessfully() throws Exception {
        String validJson = """
            {
                "smartMeterId": "integration-meter-1",
                "electricityReadings": [
                    {"time": "2024-06-01T12:00:00Z", "reading": 10.0},
                    {"time": "2024-06-01T13:00:00Z", "reading": 15.0}
                ]
            }
        """;

        mockMvc.perform(post("/readings/store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn400WhenSmartMeterIdIsMissing() throws Exception {
        String invalidJson = """
            {
                "electricityReadings": [
                    {"time": "2024-06-01T12:00:00Z", "reading": 10.0}
                ]
            }
        """;

        mockMvc.perform(post("/readings/store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation Failed"))
                .andExpect(jsonPath("$.details.smartMeterId").exists());
    }

    @Test
    void shouldReturn400WhenReadingsListIsEmpty() throws Exception {
        String invalidJson = """
            {
                "smartMeterId": "integration-meter-2",
                "electricityReadings": []
            }
        """;

        mockMvc.perform(post("/readings/store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation Failed"))
                .andExpect(jsonPath("$.details.electricityReadings").exists());
    }

    @Test
    void shouldReturn400WhenReadingIsNegative() throws Exception {
        String invalidJson = """
            {
                "smartMeterId": "integration-meter-3",
                "electricityReadings": [
                    {"time": "2024-06-01T12:00:00Z", "reading": -5.0}
                ]
            }
        """;

        mockMvc.perform(post("/readings/store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation Failed"))
                .andExpect(jsonPath("$.details['electricityReadings[0].reading']").exists());
    }

    @Test
    void shouldReturn400WhenReadingTimeIsMissing() throws Exception {
        String invalidJson = """
            {
                "smartMeterId": "integration-meter-4",
                "electricityReadings": [
                    {"reading": 10.0}
                ]
            }
        """;

        mockMvc.perform(post("/readings/store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation Failed"))
                .andExpect(jsonPath("$.details['electricityReadings[0].time']").exists());
    }
}