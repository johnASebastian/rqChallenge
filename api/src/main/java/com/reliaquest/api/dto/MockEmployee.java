package com.reliaquest.api.dto;

import com.reliaquest.api.model.Employee;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MockEmployee {
    private String id;
    private String employee_name;
    private int employee_salary;
    private int employee_age;
    private String employee_title;
    private String employee_email;


    public Employee toEmployee()
    {
        return Employee.builder()
                .id(id)
                .name(employee_name)
                .salary(employee_salary)
                .age(employee_age)
                .title(employee_title)
                .email(employee_email)
                .build();
    }

    public static MockEmployee fromEmployee(Employee employee)
    {
        return MockEmployee.builder()
                .id(employee.getId())
                .employee_name(employee.getName())
                .employee_salary(employee.getSalary())
                .employee_age(employee.getAge())
                .employee_title(employee.getTitle())
                .employee_email(employee.getEmail())
                .build();
    }
}
