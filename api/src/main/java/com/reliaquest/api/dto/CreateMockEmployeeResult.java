package com.reliaquest.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateMockEmployeeResult {
    private MockEmployee data;
    private String status;
}
