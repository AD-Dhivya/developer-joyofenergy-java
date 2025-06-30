package uk.tw.energy.util;

import org.junit.jupiter.api.Test;
import uk.tw.energy.domain.ElectricityReading;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DuplicateTimestampValidatorTest {

    @Test
    void shouldReturnFalseWhenNoDuplicateTimestamps() {
        Instant time1 = Instant.parse("2024-06-01T10:00:00Z");
        Instant time2 = Instant.parse("2024-06-01T11:00:00Z");
        
        List<ElectricityReading> readings = Arrays.asList(
            new ElectricityReading(time1, BigDecimal.valueOf(10.0)),
            new ElectricityReading(time2, BigDecimal.valueOf(15.0))
        );
        
        assertFalse(DuplicateTimestampValidator.hasDuplicateTimestamps(readings));
    }

    @Test
    void shouldReturnTrueWhenDuplicateTimestampsExist() {
        Instant time1 = Instant.parse("2024-06-01T10:00:00Z");
        Instant time2 = Instant.parse("2024-06-01T10:00:00Z"); // Same time
        
        List<ElectricityReading> readings = Arrays.asList(
            new ElectricityReading(time1, BigDecimal.valueOf(10.0)),
            new ElectricityReading(time2, BigDecimal.valueOf(15.0))
        );
        
        assertTrue(DuplicateTimestampValidator.hasDuplicateTimestamps(readings));
    }

    @Test
    void shouldReturnFalseWhenEmptyList() {
        List<ElectricityReading> readings = Arrays.asList();
        assertFalse(DuplicateTimestampValidator.hasDuplicateTimestamps(readings));
    }

    @Test
    void shouldReturnFalseWhenSingleReading() {
        Instant time1 = Instant.parse("2024-06-01T10:00:00Z");
        List<ElectricityReading> readings = Arrays.asList(
            new ElectricityReading(time1, BigDecimal.valueOf(10.0))
        );
        
        assertFalse(DuplicateTimestampValidator.hasDuplicateTimestamps(readings));
    }
} 