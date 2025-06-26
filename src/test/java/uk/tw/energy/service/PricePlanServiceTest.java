package uk.tw.energy.service;

import org.junit.jupiter.api.Test;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PricePlanServiceTest {

    @Test
    void testCostForTwoReadings() {
        ElectricityReading r1 = new ElectricityReading(Instant.parse("2025-06-23T10:00:00Z"), BigDecimal.valueOf(2.0));
        ElectricityReading r2 = new ElectricityReading(Instant.parse("2025-06-23T11:00:00Z"), BigDecimal.valueOf(4.0));

        List<ElectricityReading> electricityReadingList = List.of(r1, r2);
        PricePlan pricePlan = new PricePlan("testPlan", "testSupplier", BigDecimal.valueOf(10.0), List.of());
        String smartMeterId = "testsmartmeter";
        MeterReadingService meterReadingService = new MeterReadingService(Map.of(smartMeterId, electricityReadingList));
        PricePlanService pricePlanService = new PricePlanService(List.of(pricePlan), meterReadingService);

        Optional<Map<String, BigDecimal>> result = pricePlanService.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeterId);

        assertTrue(result.isPresent());
        BigDecimal actualcost = result.get().get("testPlan");
        System.out.println("Actual cost: " + actualcost);
        assertEquals(0, actualcost.compareTo(BigDecimal.valueOf(30.0)));


    }

    @Test
    void testCostForEmptyReadings() {

        PricePlan pricePlan = new PricePlan("testPlan", "testSupplier", BigDecimal.valueOf(10.0), List.of());
        String smartMeterId = "testsmartmeter";
        MeterReadingService meterReadingService = new MeterReadingService(Map.of(smartMeterId, List.of()));
        PricePlanService pricePlanService = new PricePlanService(List.of(pricePlan), meterReadingService);

        Optional<Map<String, BigDecimal>> result = pricePlanService.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeterId);

        assertTrue(result.isPresent());
        BigDecimal actualcost = result.get().get("testPlan");
        System.out.println("Actual cost: " + actualcost);
        assertEquals(0, actualcost.compareTo(BigDecimal.ZERO));
    }

    @Test
    void testCostForNoSmartMeterIdEmptyReadings() {

        PricePlan pricePlan = new PricePlan("testPlan", "testSupplier", BigDecimal.valueOf(10.0), List.of());
        MeterReadingService meterReadingService = new MeterReadingService(Map.of());
        PricePlanService pricePlanService = new PricePlanService(List.of(pricePlan), meterReadingService);

        Optional<Map<String, BigDecimal>> result = pricePlanService.getConsumptionCostOfElectricityReadingsForEachPricePlan("");

        assertTrue(result.isEmpty());
    }

    @Test
    void calculatesCostForMultiplePricePlans() {
        ElectricityReading r1 = new ElectricityReading(Instant.parse("2025-06-23T10:00:00Z"), BigDecimal.valueOf(2.0));
        ElectricityReading r2 = new ElectricityReading(Instant.parse("2025-06-23T11:00:00Z"), BigDecimal.valueOf(4.0));
        MeterReadingService meterReadingService = new MeterReadingService(Map.of("id", List.of(r1, r2)));
        PricePlan plan1 = new PricePlan("plan1", "supplier", BigDecimal.valueOf(10), List.of());
        PricePlan plan2 = new PricePlan("plan2", "supplier", BigDecimal.valueOf(20), List.of());
        PricePlanService service = new PricePlanService(List.of(plan1, plan2), meterReadingService);

        Optional<Map<String, BigDecimal>> result = service.getConsumptionCostOfElectricityReadingsForEachPricePlan("id");
        assertTrue(result.isPresent());
        assertEquals(0, result.get().get("plan1").compareTo(BigDecimal.valueOf(30.0)));
        assertEquals(0, result.get().get("plan2").compareTo(BigDecimal.valueOf(60.0)));
    }
} 