package uk.tw.energy.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;

@Service
public class PricePlanService {

    private final List<PricePlan> pricePlans;
    private final MeterReadingService meterReadingService;

    public PricePlanService(List<PricePlan> pricePlans, MeterReadingService meterReadingService) {
        this.pricePlans = pricePlans;
        this.meterReadingService = meterReadingService;
    }

    public Optional<Map<String, BigDecimal>> getConsumptionCostOfElectricityReadingsForEachPricePlan(
            String smartMeterId) {
        Optional<List<ElectricityReading>> electricityReadings = meterReadingService.getReadings(smartMeterId);

        if (!electricityReadings.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(pricePlans.stream()
                .collect(Collectors.toMap(PricePlan::getPlanName, t -> calculateCost(electricityReadings.get(), t))));
    }

    private BigDecimal calculateCost(List<ElectricityReading> electricityReadings, PricePlan pricePlan) {
        if (electricityReadings == null || electricityReadings.size() < 2) {
            return BigDecimal.ZERO;
        }
        // Check for negative readings
        for (ElectricityReading reading : electricityReadings) {
            if (reading.reading().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Negative electricity reading found");
            }
        }
        // Check for negative unit rate
        if (pricePlan.getUnitRate().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Negative unit rate found");
        }
        // Check for duplicate timestamps
        long uniqueTimestamps = electricityReadings.stream()
            .map(ElectricityReading::time)
            .distinct()
            .count();
        if (uniqueTimestamps < electricityReadings.size()) {
            throw new IllegalArgumentException("Duplicate timestamps found in readings");
        }
        List<ElectricityReading> sortedReadings = electricityReadings.stream()
                .sorted(Comparator.comparing(ElectricityReading::time))
                .toList();

        BigDecimal totalEnergy = BigDecimal.ZERO;
        for (int i = 0; i < sortedReadings.size() - 1; i++) {
            ElectricityReading r1 = sortedReadings.get(i);
            ElectricityReading r2 = sortedReadings.get(i + 1);
            BigDecimal avgPower = r1.reading().add(r2.reading()).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
            long seconds = Duration.between(r1.time(), r2.time()).getSeconds();
            BigDecimal hours = BigDecimal.valueOf(seconds).divide(BigDecimal.valueOf(3600), 10, RoundingMode.HALF_UP);
            totalEnergy = totalEnergy.add(avgPower.multiply(hours));
        }
        return totalEnergy.multiply(pricePlan.getUnitRate()).setScale(2,RoundingMode.HALF_UP);
    }

}
