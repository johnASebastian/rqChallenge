package com.reliaquest.api.adapter;


import com.reliaquest.api.dto.*;
import com.reliaquest.api.exceptions.MockEmployeeServiceException;
import com.reliaquest.api.model.EmployeeInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Collectors;

import static com.reliaquest.api.adapter.MockEmployeeServiceAdapter.BASE_URL;
import static com.reliaquest.api.testdata.TestDataConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class MockEmployeeServiceAdapterTest {
    private MockEmployeeServiceAdapter adapter;
    private final RestTemplate mockRestTemplate = mock(RestTemplate.class);

    @BeforeEach
    public void setup()
    {
        var builder = mock(RestTemplateBuilder.class);
        when(builder.build()).thenReturn(mockRestTemplate);
        this.adapter = new MockEmployeeServiceAdapter(builder);
    }

    @Test
    public void testGetEmployees_noErrors_mapsProperly()
    {
        when(mockRestTemplate.exchange(eq(BASE_URL), eq(GET), any(), eq(MockEmployeeListResult.class)))
                .thenReturn(new ResponseEntity<>(MockEmployeeListResult.builder()
                        .data(EMPLOYEE_LIST.stream()
                                .map(MockEmployee::fromEmployee)
                                .collect(Collectors.toList()))
                        .status("Success")
                        .build(), OK));
        var result = adapter.getEmployees();
        assertEquals(13, result.size());
        assertEquals(EMPLOYEE_LIST, result);
    }

    @Test ()
    public void testGetEmployees_serverError_returnsError()
    {
        when(mockRestTemplate.exchange(eq(BASE_URL), eq(GET), any(), eq(MockEmployeeListResult.class)))
                .thenReturn(new ResponseEntity<>(INTERNAL_SERVER_ERROR));
        try {
            adapter.getEmployees();
            fail("expected exception");
        } catch (MockEmployeeServiceException e) {
            assertTrue(e.getMessage().contains("getEmployees"));
        }
    }

    @Test ()
    public void testGetEmployees_rateLimited_mapsProperly()
    {
        when(mockRestTemplate.exchange(eq(BASE_URL), eq(GET), any(), eq(MockEmployeeListResult.class)))
                .thenReturn(
                        new ResponseEntity<>(TOO_MANY_REQUESTS),
                        new ResponseEntity<>(MockEmployeeListResult.builder()
                        .data(EMPLOYEE_LIST.stream()
                                .map(MockEmployee::fromEmployee)
                                .collect(Collectors.toList()))
                        .status("Success")
                        .build(), OK));
        adapter.getEmployees();
        var result = adapter.getEmployees();
        assertEquals(13, result.size());
        assertEquals(EMPLOYEE_LIST, result);
    }

    @Test ()
    public void testGetEmployees_rateLimitedMaxRetry_returnsError()
    {
        when(mockRestTemplate.exchange(eq(BASE_URL), eq(GET), any(), eq(MockEmployeeListResult.class)))
                .thenReturn(new ResponseEntity<>(TOO_MANY_REQUESTS));
        try {
            adapter.getEmployees();
            fail("expected exception");
        } catch (MockEmployeeServiceException e) {
            assertTrue(e.getMessage().contains("too many failures"));
        }
    }

    @Test
    public void testGetEmployee_noErrors_mapsProperly()
    {
        when(mockRestTemplate.exchange(anyString(), eq(GET), any(), eq(MockEmployeeResult.class)))
                .thenReturn(new ResponseEntity<>(MockEmployeeResult.builder()
                        .data(MockEmployee.fromEmployee(EMPLOYEE5))
                        .status("Success")
                        .build(), OK));
        var result = adapter.getEmployee(EMPLOYEE5.getId());
        assertTrue(result.isPresent());
        assertEquals(EMPLOYEE5, result.get());
    }

    @Test
    public void testGetEmployee_notFound_returnsEmptyOptional()
    {
        when(mockRestTemplate.exchange(anyString(), eq(GET), any(), eq(MockEmployeeResult.class)))
                .thenReturn(new ResponseEntity<>(NOT_FOUND));
        var result = adapter.getEmployee(EMPLOYEE5.getId());
        assertFalse(result.isPresent());
    }

    @Test
    public void testCreateEmployee_noErrors_mapsProperly()
    {
        when(mockRestTemplate.exchange(anyString(), eq(POST), any(), eq(CreateMockEmployeeResult.class)))
            .thenReturn(new ResponseEntity<>(CreateMockEmployeeResult.builder()
                    .data(MockEmployee.fromEmployee(EMPLOYEE6))
                    .status("Success")
                    .build(), OK));
        var result = adapter.createEmployee(EmployeeInput.fromEmployee(EMPLOYEE6));
        assertEquals(EMPLOYEE6, result);
    }

    @Test
    public void testDeleteEmployee_noErrors_mapsProperly()
    {
        when(mockRestTemplate.exchange(anyString(), eq(DELETE), any(), eq(DeleteMockEmployeeResult.class)))
                .thenReturn(new ResponseEntity<>(DeleteMockEmployeeResult.builder()
                        .data(true)
                        .status("Success")
                        .build(), OK));
        var result = adapter.deleteEmployee(EMPLOYEE3.getName());
        assertTrue(result);
    }
}
