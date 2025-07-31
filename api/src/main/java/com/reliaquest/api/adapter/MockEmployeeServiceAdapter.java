package com.reliaquest.api.adapter;

import com.reliaquest.api.dto.*;
import com.reliaquest.api.exceptions.MockEmployeeServiceException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@Service
public class MockEmployeeServiceAdapter {
    public static final long RETRY_BACKOFF_INTERVAL = 90000L;
    private static final Integer MAX_RETRIES = 3;
    protected static final String BASE_URL = "http://localhost:8112/api/v1/employee";
    public static final String ID = "/%s";
    private final RestTemplate restClient;
    private volatile List<Employee> employees = new ArrayList<>();

    @Autowired
    public MockEmployeeServiceAdapter(RestTemplateBuilder builder) {
        this.restClient = builder.build();
    }

    public List<Employee> getEmployees() {
        if (employees.isEmpty()) {
            var updatedEmployeeList = getEmployeeList(0);
            employees.addAll(updatedEmployeeList);
            return updatedEmployeeList;
        }
        return employees;
    }

    public Optional<Employee> getEmployee(String id) {
        return getEmployee(id, 0);
    }

    public Employee createEmployee(EmployeeInput employee) {
        employees.clear();
        return createEmployee(employee, 0);
    }

    public boolean deleteEmployee(String name) {
        employees.clear();
        return deleteEmployee(name, 0);
    }

    private Optional<Employee> getEmployee(String id, int tryCount) {
        try {
            var response =  restClient.exchange(
                String.format(BASE_URL+ID, id),
                GET, null,
                MockEmployeeResult.class);

            if (errorHandlingRetryRequired(response, tryCount++, "getEmployee")) {
                return getEmployee(id, tryCount);
            }

            var result = response.getBody();
            return Optional.ofNullable(result)
                    .map(MockEmployeeResult::getData)
                    .map(MockEmployee::toEmployee);
        } catch (HttpClientErrorException e) {
            if (errorHandlingRetryRequired(e, tryCount++, "getEmployee")) {
                return getEmployee(id, tryCount);
            }
        }
        throw new MockEmployeeServiceException("getEmployee", "error that was not handled.");
    }

    private Employee createEmployee(EmployeeInput employee, int tryCount) {
        try {
            var response =  restClient.exchange(
                    BASE_URL,
                    POST,
                    new HttpEntity<>(CreateMockEmployeeInput.fromEmployeeInput(employee)),
                    CreateMockEmployeeResult.class);

            if (errorHandlingRetryRequired(response, tryCount++, "createEmployee")) {
                return createEmployee(employee, tryCount);
            }
            var result = response.getBody();
            return Optional.ofNullable(result)
                    .map(CreateMockEmployeeResult::getData)
                    .map(MockEmployee::toEmployee)
                    .orElseThrow(() -> new MockEmployeeServiceException("createEmployee", "UNKNOWN ERROR"));
        } catch (HttpClientErrorException e) {
            if (errorHandlingRetryRequired(e, tryCount++, "createEmployee")) {
                return createEmployee(employee, tryCount);
            }
        }
        throw new MockEmployeeServiceException("deleteEmployee", "error that was not handled.");
    }

    private boolean deleteEmployee(String name, int tryCount) {
        try {
            var response =  restClient.exchange(
                    BASE_URL,
                    DELETE,
                    new HttpEntity<>(DeleteMockEmployeeInput.builder().name(name).build()),
                    DeleteMockEmployeeResult.class);

            if (errorHandlingRetryRequired(response, tryCount++, "deleteEmployee")) {
                return deleteEmployee(name, tryCount);
            }
            return Optional.ofNullable(response.getBody())
                    .map(DeleteMockEmployeeResult::isData)
                    .orElse(false);
        } catch (HttpClientErrorException e) {
            if (errorHandlingRetryRequired(e, tryCount++, "deleteEmployee")) {
                return deleteEmployee(name, tryCount);
            }
        }
        throw new MockEmployeeServiceException("deleteEmployee", "error that was not handled.");
    }

    private List<Employee> getEmployeeList(int tryCount) {
        try {
            var response =  restClient.exchange(
                    BASE_URL,
                    GET, null,
                    MockEmployeeListResult.class);

            if (errorHandlingRetryRequired(response, tryCount++, "getEmployees")) {
                return getEmployeeList(tryCount);
            }

            return Optional.ofNullable(response.getBody())
                    .map(MockEmployeeListResult::getData)
                    .map(employees -> employees.stream().map(MockEmployee::toEmployee).collect(Collectors.toList()))
                    .orElse(Collections.emptyList());
        } catch (HttpClientErrorException e) {
            if (errorHandlingRetryRequired(e, tryCount++, "getEmployees")) {
                return getEmployeeList(tryCount);
            }
        }
        throw new MockEmployeeServiceException("getEmployees", "error that was not handled.");
    }

    private void performBackoff(int tryCount, String callingMethod) {
        try {
            if (tryCount <= MAX_RETRIES) {
                Thread.sleep(tryCount * RETRY_BACKOFF_INTERVAL);
            } else {
                throw new MockEmployeeServiceException(callingMethod, "too many failures. Total Attempts:" + tryCount);
            }
        } catch (InterruptedException e2) {
            // no op if sleep fails;
        }
    }

    private boolean errorHandlingRetryRequired(ResponseEntity<?> response, int tryCount, String callingMethod){
        if (response.getStatusCode().is2xxSuccessful()) {
            return false;
        } else if (response.getStatusCode().is4xxClientError()) {
           if (response.getStatusCode().isSameCodeAs(TOO_MANY_REQUESTS)) {
               performBackoff(tryCount, callingMethod);
               return true;
           } else if (response.getStatusCode().isSameCodeAs(NOT_FOUND)){
               return false;
           } else {
               log.error("MockEmployeeClient misconfigured");
               throw new MockEmployeeServiceException(
                       callingMethod,
                       String.format("Client Returned response code [%s]", response.getStatusCode().toString()));
           }
        } else if (response.getStatusCode().is5xxServerError()) {
            if (response.getStatusCode().isSameCodeAs(SERVICE_UNAVAILABLE)) {
                performBackoff(tryCount, callingMethod);
                return true;
            }
            throw new MockEmployeeServiceException(
                    callingMethod,
                    String.format(
                            "Employee Data Server returned response code [%s]",
                            response.getStatusCode().toString()));
        }
        return false;
    }

    private boolean errorHandlingRetryRequired(HttpClientErrorException e, int tryCount, String callingMethod){
        if (e.getStatusCode().is2xxSuccessful()) {
            return false;
        } else if (e.getStatusCode().is4xxClientError()) {
            if (e.getStatusCode().isSameCodeAs(TOO_MANY_REQUESTS)) {
                performBackoff(tryCount, callingMethod);
                return true;
            } else if (e.getStatusCode().isSameCodeAs(NOT_FOUND)){
                return false;
            } else {
                log.error("MockEmployeeClient misconfigured");
                throw new MockEmployeeServiceException(
                        callingMethod,
                        String.format("Client Returned response code [%s]", e.getStatusCode().toString()));
            }
        } else if (e.getStatusCode().is5xxServerError()) {
            if (e.getStatusCode().isSameCodeAs(SERVICE_UNAVAILABLE)) {
                performBackoff(tryCount, callingMethod);
                return true;
            }
            throw new MockEmployeeServiceException(
                    callingMethod,
                    String.format(
                            "Employee Data Server returned response code [%s]",
                            e.getStatusCode().toString()));
        }
        return false;
    }


}


