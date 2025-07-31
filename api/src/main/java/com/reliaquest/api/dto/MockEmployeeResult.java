package com.reliaquest.api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MockEmployeeResult {
    private MockEmployee data;
    private String status;

}
