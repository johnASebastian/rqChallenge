package com.reliaquest.api.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class DeleteMockEmployeeResult {
    private boolean data;
    private String status;
}
