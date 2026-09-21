package com.sahana.sahanamart.dto;

import java.io.Serializable;
import java.util.Map;

public class ApiResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private T data;
    private ApiError error;

    public ApiResponse() {}

    public ApiResponse(boolean success, T data, ApiError error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, new ApiError(message, null));
    }

    public static <T> ApiResponse<T> error(String message, Map<String, String> fieldErrors) {
        return new ApiResponse<>(false, null, new ApiError(message, fieldErrors));
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ApiError getError() {
        return error;
    }

    public void setError(ApiError error) {
        this.error = error;
    }

    public static class ApiError implements Serializable {
        private static final long serialVersionUID = 1L;
        private String message;
        private Map<String, String> fields;

        public ApiError() {}

        public ApiError(String message, Map<String, String> fields) {
            this.message = message;
            this.fields = fields;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Map<String, String> getFields() {
            return fields;
        }

        public void setFields(Map<String, String> fields) {
            this.fields = fields;
        }
    }
}
