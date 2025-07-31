package com.reliaquest.api.controller;

import com.reliaquest.api.adapter.MockEmployeeServiceAdapter;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static com.reliaquest.api.testdata.TestDataConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class EmployeeControllerTest {

    @Mock
    public MockEmployeeServiceAdapter adapter;
    private IEmployeeController<Employee, EmployeeInput> employeeController;

    @BeforeEach
    public void setup() {
        employeeController = new EmployeeController(adapter);
    }

    @Test
    public void testGetEmployees_noErrors() {
        when(adapter.getEmployees()).thenReturn(EMPLOYEE_LIST);
        var result = employeeController.getAllEmployees();
        assertTrue(result.getBody() instanceof List<?>);
        assertEquals(13, ((List)result.getBody()).size());
    }

    @Test
    public void testGetEmployeesByNameSearch_noErrors() {
        when(adapter.getEmployees()).thenReturn(EMPLOYEE_LIST);
        var result = employeeController.getEmployeesByNameSearch("doe");
        assertTrue(result.getBody() instanceof List<?>);
        var listResult = (List) result.getBody();
        assertEquals(2, listResult.size());
        assertTrue(listResult.contains(EMPLOYEE4));
        assertTrue(listResult.contains(EMPLOYEE5));
    }

    @Test
    public void testGetEmployeeById_noErrors() {
        when(adapter.getEmployee(EMPLOYEE4.getId())).thenReturn(Optional.of(EMPLOYEE4));
        var result = employeeController.getEmployeeById(EMPLOYEE4.getId());
        assertTrue(result.getBody() instanceof Employee);
        assertEquals(EMPLOYEE4, result.getBody());
    }

    @Test
    public void testGetEmployeeById_notFount_returns404() {
        when(adapter.getEmployee(EMPLOYEE4.getId())).thenReturn(Optional.empty());
        var result = employeeController.getEmployeeById(EMPLOYEE4.getId());
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void testGetHighestSalary_noErrors() {
        when(adapter.getEmployees()).thenReturn(EMPLOYEE_LIST);
        var result = employeeController.getHighestSalaryOfEmployees();
        assertNotNull(result.getBody());
        assertEquals(Long.valueOf(EMPLOYEE7.getSalary()), Long.valueOf(result.getBody()));
    }

    @Test
    public void testGetTopTenHighestEarningEmployeeNames_noErrors() {
        when(adapter.getEmployees()).thenReturn(EMPLOYEE_LIST);
        var result = employeeController.getTopTenHighestEarningEmployeeNames();
        assertNotNull(result.getBody());
        var listResult = (List) result.getBody();
        assertEquals(10, listResult.size());
        assertFalse(listResult.contains(EMPLOYEE2.getName()));
        assertFalse(listResult.contains(EMPLOYEE8.getName()));
        assertFalse(listResult.contains(EMPLOYEE10.getName()));
        assertEquals(EMPLOYEE7.getName(), listResult.get(0));
        assertEquals(EMPLOYEE12.getName(), listResult.get(9));

    }

    @Test
    public void testCreateEmployee_noErrors() {
       when(adapter.createEmployee(EmployeeInput.fromEmployee(EMPLOYEE2))).thenReturn(EMPLOYEE2);
       var result = employeeController.createEmployee(EmployeeInput.fromEmployee(EMPLOYEE2));
       assertEquals(EMPLOYEE2, result.getBody());
    }

    @Test
    public void testCreateEmployee_invalidInput_returnsBadRequest() {
        var result = employeeController.createEmployee(EmployeeInput.builder().name("").title("").age(15).salary(0).build());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void testDeleteEmployee_noErrors() {
        when(adapter.getEmployee(EMPLOYEE2.getId())).thenReturn(Optional.of(EMPLOYEE2));
        when(adapter.deleteEmployee(EMPLOYEE2.getName())).thenReturn(true);
        var result = employeeController.deleteEmployeeById(EMPLOYEE2.getId());
        assertEquals(EMPLOYEE2.getName(), result.getBody());
    }

    @Test
    public void testDeleteEmployee_unknownEmployeeId_returnsNotFound() {
        when(adapter.getEmployee(EMPLOYEE2.getId())).thenReturn(Optional.empty());
        var result = employeeController.deleteEmployeeById(EMPLOYEE2.getId());
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }
}
