package com.organization.Auto_TEC.exception;

public class AiServiceException extends RuntimeException {
    private final String detail;

    public AiServiceException(String message, String detail) {
        super(message);
        this.detail = detail;
    }

    public String getDetail() {
        return detail;
    }
}
