package uk.tw.energy.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

public class PricePlan {

    @NotBlank
    private final String energySupplier;

    @NotBlank
    private final String planName;

    @NotNull @Positive private final BigDecimal unitRate; // unit price per kWh

    @NotEmpty
    private final List<@Valid PeakTimeMultiplier> peakTimeMultipliers;

    public PricePlan(
            String planName, String energySupplier, BigDecimal unitRate, List<PeakTimeMultiplier> peakTimeMultipliers) {
        this.planName = planName;
        this.energySupplier = energySupplier;
        this.unitRate = unitRate;
        this.peakTimeMultipliers = peakTimeMultipliers;
    }

    public String getEnergySupplier() {
        return energySupplier;
    }

    public String getPlanName() {
        return planName;
    }

    public BigDecimal getUnitRate() {
        return unitRate;
    }

    public BigDecimal getPrice(LocalDateTime dateTime) {
        return peakTimeMultipliers.stream()
                .filter(multiplier -> multiplier.dayOfWeek.equals(dateTime.getDayOfWeek())
                        && dateTime.getHour() >= multiplier.startHour
                        && dateTime.getHour() < multiplier.endHour)
                .findFirst()
                .map(multiplier -> unitRate.multiply(multiplier.multiplier))
                .orElse(unitRate);
    }

    public static class PeakTimeMultiplier {
        @NotNull DayOfWeek dayOfWeek;
        @Positive int startHour; // inclusive, 0-23
        @Positive int endHour;   // exclusive, 1-24
        @NotNull @Positive BigDecimal multiplier;

        public PeakTimeMultiplier(DayOfWeek dayOfWeek, int startHour, int endHour, BigDecimal multiplier) {
            this.dayOfWeek = dayOfWeek;
            this.startHour = startHour;
            this.endHour = endHour;
            this.multiplier = multiplier;
        }
    }
}
