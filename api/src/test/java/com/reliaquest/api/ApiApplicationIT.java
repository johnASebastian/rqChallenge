package com.reliaquest.api;

import com.reliaquest.api.controller.IEmployeeController;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/*
These Tests require the Mock server to be running
`./gradlew server:bootRun`
 */
@SpringBootTest
class ApiApplicationIT {

    @Autowired
    IEmployeeController<Employee, EmployeeInput> controller;

    @Test
    void tests() {
        // do the thing here
        var responseInitial = controller.getAllEmployees();
        var responseInitialList = responseInitial.getBody();
        var salaryOrderedList = responseInitialList.stream()
                .sorted(Comparator.comparingInt(Employee::getSalary))
                .toList();
        var employeeCount = responseInitialList.size();
        var highestPaidEmployee = salaryOrderedList.get(employeeCount -1);
        controller.deleteEmployeeById(salaryOrderedList.get(0).getId());
        assertEquals(employeeCount-1 , controller.getAllEmployees().getBody().size());
        assertEquals(highestPaidEmployee.getSalary(), controller.getHighestSalaryOfEmployees().getBody());
        assertEquals(List.of(salaryOrderedList.get(employeeCount -1).getName(),
                salaryOrderedList.get(employeeCount -2).getName(),
                salaryOrderedList.get(employeeCount -3).getName(),
                salaryOrderedList.get(employeeCount -4).getName(),
                salaryOrderedList.get(employeeCount -5).getName(),
                salaryOrderedList.get(employeeCount -6).getName(),
                salaryOrderedList.get(employeeCount -7).getName(),
                salaryOrderedList.get(employeeCount -8).getName(),
                salaryOrderedList.get(employeeCount -9).getName(),
                salaryOrderedList.get(employeeCount -10).getName()
                ), controller.getTopTenHighestEarningEmployeeNames().getBody());
        var createEmployee = controller.createEmployee(EmployeeInput.builder()
                .age(55)
                .salary(9999999)
                .name("Money Bags")
                .title("Makes The Most")
                .build()).getBody();
        assertEquals(createEmployee.getSalary(), controller.getHighestSalaryOfEmployees().getBody());
        assertEquals(createEmployee, controller.getEmployeeById(createEmployee.getId()).getBody());
        assertEquals(responseInitialList.size(), controller.getAllEmployees().getBody().size());
        assertEquals(createEmployee.getName(), controller.getTopTenHighestEarningEmployeeNames().getBody().get(0));
    }
}
