package uk.tw.energy.domain;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public record Interval(@NotNull ElectricityReading start,
                       @NotNull ElectricityReading end){

    public BigDecimal buildAvgPower(){
    return start().reading().add(end().reading()).divide(BigDecimal.valueOf(2),10,RoundingMode.HALF_UP);
    }
    public BigDecimal buildDurationInHours(){
    long second = Duration.between(start.time(), end().time())
            .getSeconds();
    return BigDecimal.valueOf(second).divide(BigDecimal.valueOf(3600),10,RoundingMode.HALF_UP);
    }
    public LocalDateTime buildAvgTime(){
        long epochSec = (start().time().getEpochSecond()+end().time().getEpochSecond())/2;
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSec),ZoneOffset.UTC);
    }
}