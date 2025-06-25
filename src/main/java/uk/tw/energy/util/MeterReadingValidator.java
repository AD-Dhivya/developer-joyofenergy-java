package uk.tw.energy.util;

import uk.tw.energy.domain.ElectricityReading;

import java.math.BigDecimal;
import java.time.Instant;

public class MeterReadingValidator {
    public static boolean isValid(ElectricityReading reading){
        return  reading!=null&&
                reading.time()!=null&&
                reading.reading()!=null&&
                reading.reading().compareTo(BigDecimal.ZERO)>=0&&
                reading.time().isBefore(Instant.now());
    }
}
