package com.reliaquest.api.exceptions;


import lombok.Getter;

public class MockEmployeeServiceRequestException extends RuntimeException {
    public static final String ERROR_MESSAGE = "Error with request to Mock Employee Serivce.";
    @Getter
    private int statusCode;
    public MockEmployeeServiceRequestException(int statusCode, String restType, String uri) {
        super(String.format(ERROR_MESSAGE + " Method [%s] URI [%s] Status [%s]",  restType, uri, statusCode));
        this.statusCode = statusCode;
    }
}
