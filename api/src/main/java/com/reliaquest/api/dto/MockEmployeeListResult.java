package com.reliaquest.api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MockEmployeeListResult {
    private List<MockEmployee> data;
    private String status;

}
