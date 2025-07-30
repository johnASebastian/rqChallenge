package com.reliaquest.api.adapter;

import com.reliaquest.api.dto.CreateMockEmployeeInput;
import com.reliaquest.api.dto.MockEmployee;
import com.reliaquest.api.dto.MockEmployeeListResult;
import com.reliaquest.api.dto.MockEmployeeResult;
import com.reliaquest.api.exceptions.MockEmployeeServiceException;
import com.reliaquest.api.exceptions.MockEmployeeServiceRequestException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MockEmployeeServiceAdapter {
    public static final int CONNECTION_REQUEST_TIMEOUT = 5000;
    public static final int RETRY_BACKOFF_INTERVAL = 500;
    private static final Integer MAX_RETRIES = 3;
    private final RestClient restClient;

    public MockEmployeeServiceAdapter(RestClient.Builder restClientBuilder) {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT);
        this.restClient = restClientBuilder
                .requestFactory(requestFactory)
                .baseUrl("http://localhost:8112/api/v1/employee")
                .build();
    }

    public List<Employee> getEmployees() {
        return getEmployeeList(0);
    }

    public Optional<Employee> getEmployee(String id) {
        return getEmployee(id, 0);
    }

    public Employee createEmployee(EmployeeInput employee)
    {
        return createEmployee(employee, 0);
    }

    private Optional<Employee> getEmployee(String id, int tryCount) {
        try {
            var request =  restClient.get().uri("/{id}/", id).retrieve();
            setupErrorHandling(request);
            var result = request.body(MockEmployeeResult.class);
            return Optional.ofNullable(result.getData().toEmployee());
        } catch (MockEmployeeServiceRequestException e) {
            if (tryCount < MAX_RETRIES)
            {
                return getEmployee(id, performBackoff(tryCount));
            }
            else
            {
                throw new MockEmployeeServiceException("getEmployee", e);
            }
        }
    }

    private Employee createEmployee(EmployeeInput employee, int tryCount) {
        try {
            var request =  restClient.post().body(CreateMockEmployeeInput.fromEmployeeInput(employee)).retrieve();
            setupErrorHandling(request);
            var result = request.body(MockEmployeeResult.class);
            return result.getData().toEmployee();
        } catch (MockEmployeeServiceRequestException e) {
            if (tryCount < MAX_RETRIES)
            {
                return createEmployee(employee, performBackoff(tryCount));
            }
            else
            {
                throw new MockEmployeeServiceException("createEmployee", e);
            }
        }
    }



    private Employee deleteEmployee(String id, int tryCount) {
        try {
            var request =  restClient.post().body(CreateMockEmployeeInput.fromEmployeeInput(employee)).retrieve();
            setupErrorHandling(request);
            var result = request.body(MockEmployeeResult.class);
            return result.getData().toEmployee();
        } catch (MockEmployeeServiceRequestException e) {
            if (tryCount < MAX_RETRIES)
            {
                return createEmployee(employee, performBackoff(tryCount));
            }
            else
            {
                throw new MockEmployeeServiceException("getEmployee", e);
            }
        }
    }


    private List<Employee> getEmployeeList(int tryCount) {
        try {
            var request = restClient.get().retrieve();
            setupErrorHandling(request);
            var result = request.body(MockEmployeeListResult.class);
            return result.getData().stream().map(MockEmployee::toEmployee).collect(Collectors.toList());
        } catch (MockEmployeeServiceRequestException e) {
            if (tryCount < MAX_RETRIES)
            {
                return getEmployeeList(performBackoff(tryCount));
            }
            else
            {
                throw new RuntimeException("Error Retrieving Data from Server.", e);
            }
        }
    }

    private int performBackoff(int tryCount) {
        tryCount++;
        try {
            Thread.sleep(tryCount * RETRY_BACKOFF_INTERVAL);
        }
        catch (InterruptedException e2)
        {
            // no op if sleep fails;
        }
        return tryCount;
    }

    private void setupErrorHandling(RestClient.ResponseSpec httpRequest) {
        httpRequest.onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
                    throw new MockEmployeeServiceRequestException(
                            response.getStatusCode().value(),
                            request.getMethod().name(),
                            request.getURI().toString());
                }))
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) -> {
                    throw new MockEmployeeServiceRequestException(
                            response.getStatusCode().value(),
                            request.getMethod().name(),
                            request.getURI().toString());
                }));
    }


}


