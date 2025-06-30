package uk.tw.energy.builders;

import java.util.ArrayList;
import java.util.List;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.generator.ElectricityReadingsGenerator;
import uk.tw.energy.util.DuplicateTimestampValidator;

public class MeterReadingsBuilder {

    private static final String DEFAULT_METER_ID = "id";

    private String smartMeterId = DEFAULT_METER_ID;
    private List<ElectricityReading> electricityReadings = new ArrayList<>();

    public MeterReadingsBuilder setSmartMeterId(String smartMeterId) {
        this.smartMeterId = smartMeterId;
        return this;
    }

    public MeterReadingsBuilder generateElectricityReadings() {
        return generateElectricityReadings(5);
    }

    public MeterReadingsBuilder generateElectricityReadings(int number) {
        ElectricityReadingsGenerator readingsBuilder = new ElectricityReadingsGenerator();
        this.electricityReadings = readingsBuilder.generate(number);
        return this;
    }

    public MeterReadings build() {
        // Validate that test data doesn't contain duplicates
        if (!electricityReadings.isEmpty() && DuplicateTimestampValidator.hasDuplicateTimestamps(electricityReadings)) {
            throw new IllegalStateException("Test data contains duplicate timestamps");
        }
        return new MeterReadings(smartMeterId, electricityReadings);
    }
}
