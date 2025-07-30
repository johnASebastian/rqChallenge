package com.reliaquest.api.dto;

import lombok.Data;

@Data
public class CreateMockEmployeeResult {
    private MockEmployee data;
    private String status;
}
