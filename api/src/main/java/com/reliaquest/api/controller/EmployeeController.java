package com.reliaquest.api.controller;

import com.reliaquest.api.adapter.MockEmployeeServiceAdapter;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import static java.util.stream.Collectors.toList;

@RestController
@RequiredArgsConstructor
public class EmployeeController implements IEmployeeController<Employee, EmployeeInput> {

    private final MockEmployeeServiceAdapter mockEmployeeService;

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok().body(mockEmployeeService.getEmployees());
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        return ResponseEntity.ok().body(mockEmployeeService.getEmployees()
                .stream()
                .filter(employee -> employee.getName().toLowerCase().contains(searchString.toLowerCase()))
                .collect(toList()));
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        return mockEmployeeService.getEmployee(id)
                .map(employee -> ResponseEntity.ok().body(employee))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        var sortedBySalary = getEmployeesSortedBySalary();
        return sortedBySalary.stream()
                .findFirst()
                .map(employee -> ResponseEntity.ok().body(employee.getSalary()))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        return  ResponseEntity.ok()
                .body(getEmployeesSortedBySalary()
                        .subList(0,10)
                        .stream()
                        .map(Employee::getName)
                        .collect(toList()));
    }

    @Override
    public ResponseEntity<Employee> createEmployee(EmployeeInput employeeInput) {
        var errors = validateCreate(employeeInput);
        if (!errors.isEmpty())
        {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(mockEmployeeService.createEmployee(employeeInput));
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        var employeeToDelete = mockEmployeeService.getEmployee(id).map(Employee::getName);
        if (employeeToDelete.isPresent()) {

            if (mockEmployeeService.deleteEmployee(employeeToDelete.get())) {
                return ResponseEntity.ok().body(employeeToDelete.get());
            } else {
                return ResponseEntity.internalServerError().build();
            }
        }
        return ResponseEntity.notFound().build();
    }

    private List<Employee> getEmployeesSortedBySalary() {
        var sortedBySalary = mockEmployeeService.getEmployees();
        sortedBySalary.sort((emp1, emp2) -> Integer.compare(emp2.getSalary(), emp1.getSalary()));
        return sortedBySalary;
    }

    private String validateCreate(EmployeeInput input)
    {
        var errors = "";
        if (input.getName() == null) {
            errors += "Name cannot be null";
        } else if (input.getName().trim().isEmpty()) {
            errors += "Name must be longer than 1";
        }
        if (input.getSalary() == null) {
            errors += "Salary cannot be null";
        } else if (input.getSalary() <= 0) {
            errors += "Salary must be greater than 0";
        }
        if (input.getAge() == null) {
            errors += "Age cannot be null";
        } else if (input.getAge() < 16  || input.getAge() > 75) {
            errors += "Age range is 16 to 75 inclusive";
        }
        if (input.getTitle() == null) {
            errors += "Title cannot be null";
        } else if (input.getTitle().trim().isEmpty()) {
            errors += "Title must be longer than 1";
        }
        return errors;

    }
}
