package uk.tw.energy.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MeterReadingControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnAllReadingsWhenNoQueryParams() throws Exception {
        // Assume readings are already stored for "test-meter"
        mockMvc.perform(get("/readings/read/test-meter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1)))); // Adjust as needed
    }

    @Test
    void shouldReturnReadingsFromDate() throws Exception {
        mockMvc.perform(get("/readings/read/test-meter")
                        .param("from", "2024-06-01T12:00:00Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].time").value("2024-06-01T12:00:00Z"));
    }

    @Test
    void shouldReturnReadingsToDate() throws Exception {
        mockMvc.perform(get("/readings/read/test-meter")
                        .param("to", "2024-06-01T12:00:00Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[-1:].time").value("2024-06-01T12:00:00Z"));
    }

    @Test
    void shouldReturnReadingsInRange() throws Exception {
        mockMvc.perform(get("/readings/read/test-meter")
                        .param("from", "2024-06-01T10:00:00Z")
                        .param("to", "2024-06-01T12:00:00Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1)))); // Adjust as needed
    }

    @Test
    void shouldReturn400ForInvalidDateFormat() throws Exception {
        mockMvc.perform(get("/readings/read/test-meter")
                        .param("from", "not-a-date"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid date format for 'from' or 'to'. Use ISO-8601 format."));
    }

    @Test
    void shouldReturn400WhenFromAfterTo() throws Exception {
        mockMvc.perform(get("/readings/read/test-meter")
                        .param("from", "2024-06-02T12:00:00Z")
                        .param("to", "2024-06-01T12:00:00Z"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("'from' date must be before 'to' date."));
    }

    @Test
    void shouldReturn404WhenMeterNotFound() throws Exception {
        mockMvc.perform(get("/readings/read/nonexistent-meter"))
                .andExpect(status().isNotFound());
    }
}