package com.reliaquest.api.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;


@Data
@Builder
public class EmployeeInput {
    private String name;
    private Integer salary;
    private Integer age;
    private String title;

    public static EmployeeInput fromEmployee(Employee employee)
    {
        return EmployeeInput.builder()
                .age(employee.getAge())
                .name(employee.getName())
                .salary(employee.getSalary())
                .title(employee.getTitle())
                .build();
    }
}
