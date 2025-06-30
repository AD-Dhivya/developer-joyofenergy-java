package uk.tw.energy.domain;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;

public class PricePlanTest {

    private final String ENERGY_SUPPLIER_NAME = "Energy Supplier Name";

    @Test
    public void shouldReturnTheEnergySupplierGivenInTheConstructor() {
        PricePlan pricePlan = new PricePlan(null, ENERGY_SUPPLIER_NAME, null, null);

        assertThat(pricePlan.getEnergySupplier()).isEqualTo(ENERGY_SUPPLIER_NAME);
    }

    @Test
    public void shouldReturnTheBasePriceGivenAnOrdinaryDateTime() throws Exception {
        LocalDateTime normalDateTime = LocalDateTime.of(2017, Month.AUGUST, 31, 12, 0, 0);
        PricePlan.PeakTimeMultiplier peakTimeMultiplier =
                new PricePlan.PeakTimeMultiplier(DayOfWeek.WEDNESDAY, BigDecimal.TEN);
        PricePlan pricePlan = new PricePlan(null, null, BigDecimal.ONE, singletonList(peakTimeMultiplier));

        BigDecimal price = pricePlan.getPrice(normalDateTime);

        assertThat(price).isCloseTo(BigDecimal.ONE, Percentage.withPercentage(1));
    }

    @Test
    public void shouldReturnAnExceptionPriceGivenExceptionalDateTime() throws Exception {
        LocalDateTime exceptionalDateTime = LocalDateTime.of(2017, Month.AUGUST, 30, 23, 0, 0);
        PricePlan.PeakTimeMultiplier peakTimeMultiplier =
                new PricePlan.PeakTimeMultiplier(DayOfWeek.WEDNESDAY, BigDecimal.TEN);
        PricePlan pricePlan = new PricePlan(null, null, BigDecimal.ONE, singletonList(peakTimeMultiplier));

        BigDecimal price = pricePlan.getPrice(exceptionalDateTime);

        assertThat(price).isCloseTo(BigDecimal.TEN, Percentage.withPercentage(1));
    }

    @Test
    public void shouldReceiveMultipleExceptionalDateTimes() throws Exception {
        LocalDateTime exceptionalDateTime = LocalDateTime.of(2017, Month.AUGUST, 30, 23, 0, 0);
        PricePlan.PeakTimeMultiplier peakTimeMultiplier =
                new PricePlan.PeakTimeMultiplier(DayOfWeek.WEDNESDAY, BigDecimal.TEN);
        PricePlan.PeakTimeMultiplier otherPeakTimeMultiplier =
                new PricePlan.PeakTimeMultiplier(DayOfWeek.TUESDAY, BigDecimal.TEN);
        List<PricePlan.PeakTimeMultiplier> peakTimeMultipliers =
                Arrays.asList(peakTimeMultiplier, otherPeakTimeMultiplier);
        PricePlan pricePlan = new PricePlan(null, null, BigDecimal.ONE, peakTimeMultipliers);

        BigDecimal price = pricePlan.getPrice(exceptionalDateTime);

        assertThat(price).isCloseTo(BigDecimal.TEN, Percentage.withPercentage(1));
    }

    @Test
    public void testPeakAndOffPeakPricingByHour() {
        PricePlan.PeakTimeMultiplier peak = new PricePlan.PeakTimeMultiplier(DayOfWeek.MONDAY, 18, 22, BigDecimal.valueOf(2.0));
        PricePlan.PeakTimeMultiplier offPeak = new PricePlan.PeakTimeMultiplier(DayOfWeek.MONDAY, 0, 18, BigDecimal.valueOf(1.0));
        PricePlan plan = new PricePlan("test", "supplier", BigDecimal.valueOf(10), List.of(peak, offPeak));

        // 7pm Monday (peak)
        LocalDateTime peakTime = LocalDateTime.of(2024, 6, 10, 19, 0);
        assertThat(plan.getPrice(peakTime)).isEqualTo(BigDecimal.valueOf(20));

        // 10am Monday (off-peak)
        LocalDateTime offPeakTime = LocalDateTime.of(2024, 6, 10, 10, 0);
        assertThat(plan.getPrice(offPeakTime)).isEqualTo(BigDecimal.valueOf(10));
    }
}
