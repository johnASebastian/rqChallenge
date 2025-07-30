package com.reliaquest.api.dto;

import com.reliaquest.api.model.EmployeeInput;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateMockEmployeeInput {
    private String name;
    private Integer salary;
    private Integer age;
    private String title;

    public static CreateMockEmployeeInput fromEmployeeInput(EmployeeInput input)
    {
        return CreateMockEmployeeInput.builder()
                .name(input.getName())
                .salary(input.getSalary())
                .age(input.getAge())
                .title(input.getTitle())
                .build();
    }
}
