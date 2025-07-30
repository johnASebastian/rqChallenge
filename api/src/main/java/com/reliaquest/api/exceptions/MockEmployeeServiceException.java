package com.reliaquest.api.exceptions;


import lombok.Getter;

public class MockEmployeeServiceException extends RuntimeException {
    public static final String ERROR_MESSAGE = "Error with request to Mock Employee Serivce. Error source [%s]";
    public MockEmployeeServiceException(String callingMethod, Throwable e) {
        super(String.format(ERROR_MESSAGE, callingMethod), e);
    }
}
