package ru.practicum.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import ru.practicum.event.UtilConstants;

import java.time.LocalDateTime;


@Data
@Builder
public class ErrorDto {

    private HttpStatus status;

    private String reason;

    private String message;

    private StackTraceElement[] errors;

    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = UtilConstants.DATETIME_FORMAT)
    private LocalDateTime errorTimestamp;
}