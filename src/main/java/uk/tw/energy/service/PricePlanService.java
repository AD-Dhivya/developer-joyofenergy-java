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
import uk.tw.energy.domain.Interval;
import uk.tw.energy.domain.PricePlan;
import uk.tw.energy.util.IntervalBuilder;
import uk.tw.energy.util.DuplicateTimestampValidator;

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
        if (DuplicateTimestampValidator.hasDuplicateTimestamps(electricityReadings)) {
            throw new IllegalArgumentException("Duplicate timestamps found in readings");
        }
        List<ElectricityReading> sortedReadings = electricityReadings.stream().sorted(
                Comparator.comparing(ElectricityReading::time)
        ).toList();
        IntervalBuilder intervalBuilder = new IntervalBuilder();
        BigDecimal totalcost = BigDecimal.ZERO;

       List<Interval> intervalList =  intervalBuilder.buildIntervalList(sortedReadings);
       for(Interval interval: intervalList){
        BigDecimal intervalRate = pricePlan.getPrice(interval.buildAvgTime());
        BigDecimal energy = interval.buildAvgPower().multiply(interval.buildDurationInHours());
       totalcost = totalcost.add(energy.multiply(intervalRate));
       }
        return totalcost.setScale(2,RoundingMode.HALF_UP);
    }

    private BigDecimal calculateAverageReading(List<ElectricityReading> electricityReadings) {
        BigDecimal summedReadings = electricityReadings.stream()
                .map(ElectricityReading::reading)
                .reduce(BigDecimal.ZERO, (reading, accumulator) -> reading.add(accumulator));

        return summedReadings.divide(BigDecimal.valueOf(electricityReadings.size()), RoundingMode.HALF_UP);
    }

    private BigDecimal calculateUsageTimeInHours(List<ElectricityReading> electricityReadings) {
        ElectricityReading first = electricityReadings.stream()
                .min(Comparator.comparing(ElectricityReading::time))
                .get();

        ElectricityReading last = electricityReadings.stream()
                .max(Comparator.comparing(ElectricityReading::time))
                .get();

        return BigDecimal.valueOf(Duration.between(first.time(), last.time()).getSeconds() / 3600.0);
    }
}
