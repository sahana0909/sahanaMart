package com.sahana.sahanamart.exception;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final Map<String, String> fieldErrors;

    public ValidationException(String message) {
        super(message);
        this.fieldErrors = Collections.emptyMap();
    }

    public ValidationException(String message, Map<String, String> fieldErrors) {
        super(message + (fieldErrors != null && !fieldErrors.isEmpty() ? ": " + fieldErrors : ""));
        this.fieldErrors = fieldErrors != null ? new HashMap<>(fieldErrors) : Collections.emptyMap();
    }

    public ValidationException(String field, String error) {
        super("Validation error on field '" + field + "': " + error);
        Map<String, String> map = new HashMap<>();
        map.put(field, error);
        this.fieldErrors = map;
    }

    public Map<String, String> getFieldErrors() {
        return Collections.unmodifiableMap(fieldErrors);
    }
}
