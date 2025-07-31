package com.reliaquest.api.testdata;

import com.reliaquest.api.model.Employee;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestDataConstants {

    public static final Employee EMPLOYEE1 = Employee.builder()
            .id(UUID.randomUUID().toString())
            .name("Tiger Nixon")
            .age(61)
            .salary(320800)
            .email("tnixon@company.com")
            .title("Vice Chair Executive Principal of Chief Operations Implementation Specialist")
            .build();
    public static final Employee EMPLOYEE2 = Employee.builder()
            .id(UUID.randomUUID().toString())
            .name("Bill Bob")
            .age(24)
            .salary(89750)
            .email("billBob@company.com")
            .title("Documentation Engineer")
            .build();
    public static final Employee EMPLOYEE3 = Employee.builder()
            .id(UUID.randomUUID().toString())
            .name("Jill Jenkins")
            .age(48)
            .salary(139082)
            .email("jillj@company.com")
            .title("Financial Advisor")
            .build();
    public static final Employee EMPLOYEE4 = Employee.builder()
            .id(UUID.randomUUID().toString())
            .name("Jane Doe")
            .age(40)
            .salary(159082)
            .email("janed@company.com")
            .title("Technical Advisor")
            .build();
    public static final Employee EMPLOYEE5 = Employee.builder()
            .id(UUID.randomUUID().toString())
            .name("John Doe")
            .age(25)
            .salary(149082)
            .email("jDoe@company.com")
            .title("Financial Auditor")
            .build();
    public static final Employee EMPLOYEE6 = Employee.builder()
            .id(UUID.randomUUID().toString())
            .name("James Dean")
            .age(51)
            .salary(199082)
            .email("jdean@company.com")
            .title("Culinary Advisor")
            .build();
    public static final Employee EMPLOYEE7 = Employee.builder()
            .id(UUID.randomUUID().toString())
            .name("Old Man")
            .age(99)
            .salary(999999)
            .email("oman@company.com")
            .title("The Old Man")
            .build();
    public static final Employee EMPLOYEE8 = Employee.builder()
            .id(UUID.randomUUID().toString())
            .name("Young Gun")
            .age(16)
            .salary(1000)
            .email("ygun@company.com")
            .title("The Rookie")
            .build();
    public static final Employee EMPLOYEE9 = Employee.builder()
            .id(UUID.randomUUID().toString())
            .name("Joe Bob")
            .age(34)
            .salary(234678)
            .email("jbob@company.com")
            .title("The Fixer")
            .build();
    public static final Employee EMPLOYEE10 = Employee.builder()
            .id(UUID.randomUUID().toString())
            .name("Doug Trio")
            .age(20)
            .salary(109082)
            .email("dtrio@company.com")
            .title("Facilities Management")
            .build();
    public static final Employee EMPLOYEE11 = Employee.builder()
            .id(UUID.randomUUID().toString())
            .name("Chadd Carlson")
            .age(30)
            .salary(129082)
            .email("chaddc@company.com")
            .title("Innovation Specialist")
            .build();
    public static final Employee EMPLOYEE12 = Employee.builder()
            .id(UUID.randomUUID().toString())
            .name("Mary Sue")
            .age(38)
            .salary(119082)
            .email("msue@company.com")
            .title("Technical Implementor")
            .build();
    public static final Employee EMPLOYEE13 = Employee.builder()
            .id(UUID.randomUUID().toString())
            .name("Walter White")
            .age(49)
            .salary(339082)
            .email("wwhite@company.com")
            .title("Chemical Handling Technician")
            .build();
    public static List<Employee> EMPLOYEE_LIST = new ArrayList<Employee>();
    static
    {
        EMPLOYEE_LIST.add(EMPLOYEE1);
        EMPLOYEE_LIST.add(EMPLOYEE2);
        EMPLOYEE_LIST.add(EMPLOYEE3);
        EMPLOYEE_LIST.add(EMPLOYEE4);
        EMPLOYEE_LIST.add(EMPLOYEE5);
        EMPLOYEE_LIST.add(EMPLOYEE6);
        EMPLOYEE_LIST.add(EMPLOYEE7);
        EMPLOYEE_LIST.add(EMPLOYEE8);
        EMPLOYEE_LIST.add(EMPLOYEE9);
        EMPLOYEE_LIST.add(EMPLOYEE10);
        EMPLOYEE_LIST.add(EMPLOYEE11);
        EMPLOYEE_LIST.add(EMPLOYEE12);
        EMPLOYEE_LIST.add(EMPLOYEE13);
    }
            
}
