package com.reliaquest.api.model;

import lombok.Data;

@Data
public class EmployeeInput {
    private String name;
    private Integer salary;
    private Integer age;
    private String title;
}
