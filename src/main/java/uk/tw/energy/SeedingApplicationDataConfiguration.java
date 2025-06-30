package uk.tw.energy;

import static java.util.Collections.emptyList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;
import uk.tw.energy.generator.ElectricityReadingsGenerator;
import uk.tw.energy.util.DuplicateTimestampValidator;

@Configuration
public class SeedingApplicationDataConfiguration {

    private static final String MOST_EVIL_PRICE_PLAN_ID = "price-plan-0";
    private static final String RENEWABLES_PRICE_PLAN_ID = "price-plan-1";
    private static final String STANDARD_PRICE_PLAN_ID = "price-plan-2";

    @Bean
    public List<PricePlan> pricePlans() {
        final List<PricePlan> pricePlans = new ArrayList<>();
        List<PricePlan.PeakTimeMultiplier> evilMultipliers = List.of(
            new PricePlan.PeakTimeMultiplier(DayOfWeek.MONDAY, 18, 22, BigDecimal.valueOf(2.0)), // Peak 6pm-10pm
            new PricePlan.PeakTimeMultiplier(DayOfWeek.MONDAY, 0, 18, BigDecimal.valueOf(1.0)),  // Off-peak
            new PricePlan.PeakTimeMultiplier(DayOfWeek.MONDAY, 22, 24, BigDecimal.valueOf(1.0))  // Off-peak
        );
        List<PricePlan.PeakTimeMultiplier> greenMultipliers = List.of(
            new PricePlan.PeakTimeMultiplier(DayOfWeek.TUESDAY, 7, 10, BigDecimal.valueOf(1.5)), // Peak 7am-10am
            new PricePlan.PeakTimeMultiplier(DayOfWeek.TUESDAY, 0, 7, BigDecimal.valueOf(1.0)),  // Off-peak
            new PricePlan.PeakTimeMultiplier(DayOfWeek.TUESDAY, 10, 24, BigDecimal.valueOf(1.0)) // Off-peak
        );
        List<PricePlan.PeakTimeMultiplier> standardMultipliers = List.of(
            new PricePlan.PeakTimeMultiplier(DayOfWeek.WEDNESDAY, 17, 20, BigDecimal.valueOf(1.8)), // Peak 5pm-8pm
            new PricePlan.PeakTimeMultiplier(DayOfWeek.WEDNESDAY, 0, 17, BigDecimal.valueOf(1.0)),  // Off-peak
            new PricePlan.PeakTimeMultiplier(DayOfWeek.WEDNESDAY, 20, 24, BigDecimal.valueOf(1.0))  // Off-peak
        );
        pricePlans.add(new PricePlan(MOST_EVIL_PRICE_PLAN_ID, "Dr Evil's Dark Energy", BigDecimal.TEN, evilMultipliers));
        pricePlans.add(new PricePlan(RENEWABLES_PRICE_PLAN_ID, "The Green Eco", BigDecimal.valueOf(2), greenMultipliers));
        pricePlans.add(new PricePlan(STANDARD_PRICE_PLAN_ID, "Power for Everyone", BigDecimal.ONE, standardMultipliers));
        return pricePlans;
    }

    @Bean
    public Map<String, List<ElectricityReading>> perMeterElectricityReadings() {
        final Map<String, List<ElectricityReading>> readings = new HashMap<>();
        final ElectricityReadingsGenerator electricityReadingsGenerator = new ElectricityReadingsGenerator();
        smartMeterToPricePlanAccounts()
                .keySet()
                .forEach(smartMeterId -> {
                    List<ElectricityReading> meterReadings = electricityReadingsGenerator.generate(20);
                    // Validate seeded data doesn't contain duplicates
                    if (DuplicateTimestampValidator.hasDuplicateTimestamps(meterReadings)) {
                        throw new IllegalStateException("Seeded data for " + smartMeterId + " contains duplicate timestamps");
                    }
                    readings.put(smartMeterId, meterReadings);
                });
        return readings;
    }

    @Bean
    public Map<String, String> smartMeterToPricePlanAccounts() {
        final Map<String, String> smartMeterToPricePlanAccounts = new HashMap<>();
        smartMeterToPricePlanAccounts.put("smart-meter-0", MOST_EVIL_PRICE_PLAN_ID);
        smartMeterToPricePlanAccounts.put("smart-meter-1", RENEWABLES_PRICE_PLAN_ID);
        smartMeterToPricePlanAccounts.put("smart-meter-2", MOST_EVIL_PRICE_PLAN_ID);
        smartMeterToPricePlanAccounts.put("smart-meter-3", STANDARD_PRICE_PLAN_ID);
        smartMeterToPricePlanAccounts.put("smart-meter-4", RENEWABLES_PRICE_PLAN_ID);
        return smartMeterToPricePlanAccounts;
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }
}
