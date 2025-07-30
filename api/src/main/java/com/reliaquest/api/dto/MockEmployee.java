package com.reliaquest.api.dto;

import com.reliaquest.api.model.Employee;
import lombok.Data;

@Data
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
}
