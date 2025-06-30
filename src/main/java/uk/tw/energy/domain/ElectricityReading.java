package uk.tw.energy.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * @param reading kW
 */
public record ElectricityReading(@NotNull @PastOrPresent Instant time, @NotNull @Positive BigDecimal reading) {}
