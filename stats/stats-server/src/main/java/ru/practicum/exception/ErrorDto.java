package ru.practicum.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static ru.practicum.DateTimeFormatConstants.DATE_TIME_FORMAT;

@Data
@Builder
public class ErrorDto {

    private HttpStatus status;

    private String reason;

    private String message;

    private StackTraceElement[] errors;

    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    private LocalDateTime errorTimestamp;
}