package uk.tw.energy.util;

import uk.tw.energy.domain.ElectricityReading;
import java.util.List;

public class DuplicateTimestampValidator {
    public static boolean hasDuplicateTimestamps(List<ElectricityReading> readings) {
        long uniqueCount = readings.stream()
            .map(ElectricityReading::time)
            .distinct()
            .count();
        return uniqueCount < readings.size();
    }
} 