package uk.tw.energy.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record APIErrorResponse(LocalDateTime timestamp, int status, String path, Map<String, String> errors) {
}
