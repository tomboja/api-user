package com.olmaitsolutions.edu.boja.apiusers.apiusers.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.List;

@Value
@Builder(toBuilder = true)
public class ApiError {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    OffsetDateTime timestamp;

    int status;

    String error;

    String message;

    String path;

    List<FieldError> fieldErrors;

    @Value
    @Builder
    public static class FieldError {
        String field;
        String message;
        Object rejectedValue;
    }
}
