package com.reliaquest.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class MockEmployeeListResult {
    private List<MockEmployee> data;
    private String status;

}
