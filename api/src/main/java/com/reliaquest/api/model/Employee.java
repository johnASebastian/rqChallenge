package com.reliaquest.api.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Employee {
    private String id;
    private String name;
    private int salary;
    private int age;
    private String title;
    private String email;
}
