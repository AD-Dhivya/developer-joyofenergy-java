package uk.tw.energy.controller;

import java.util.List;
import java.util.Optional;
import java.time.Instant;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.service.MeterReadingService;
import uk.tw.energy.util.DuplicateTimestampValidator;


@RestController
@RequestMapping("/readings")
public class MeterReadingController {

    private final MeterReadingService meterReadingService;

    public MeterReadingController(MeterReadingService meterReadingService) {
        this.meterReadingService = meterReadingService;
    }

    @PostMapping("/store")
    public ResponseEntity<Void> storeReadings(@Valid @RequestBody MeterReadings meterReadings) {
        if (DuplicateTimestampValidator.hasDuplicateTimestamps(meterReadings.electricityReadings())) {
            throw new IllegalArgumentException("Duplicate timestamps found in readings");
        }
        meterReadingService.storeReadings(meterReadings.smartMeterId(), meterReadings.electricityReadings());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/read/{smartMeterId}")
    public ResponseEntity<List<ElectricityReading>> readReadings(
            @PathVariable String smartMeterId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        Optional<List<ElectricityReading>> readingsOpt = meterReadingService.getReadings(smartMeterId);
        if (readingsOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<ElectricityReading> filtered = filterReadingsByDate(readingsOpt.get(), from, to);
        return ResponseEntity.ok(filtered);
    }

    private List<ElectricityReading> filterReadingsByDate(List<ElectricityReading> readings, String from, String to) {
        Instant fromInstant = null;
        Instant toInstant = null;
        try {
            if (from != null) fromInstant = Instant.parse(from);
            if (to != null) toInstant = Instant.parse(to);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format for 'from' or 'to'. Use ISO-8601 format.");
        }
        if (fromInstant != null && toInstant != null && fromInstant.isAfter(toInstant)) {
            throw new IllegalArgumentException("'from' date must be before 'to' date.");
        }
        Instant finalFromInstant = fromInstant;
        Instant finalToInstant = toInstant;
        return readings.stream()
            .filter(r -> (finalFromInstant == null || !r.time().isBefore(finalFromInstant)) &&
                         (finalToInstant == null || !r.time().isAfter(finalToInstant)))
            .collect(Collectors.toList());
    }
}
