package com.auth.dto.response;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionResponseDto {

    long id;

    @NotBlank(message = "Name can not be empty")
    @Size(min = 1, max = 25, message = "Permission name must be between 1 and 25 characters")
    String name;

    @NotBlank(message = "Description can not be empty")
    @Size(min = 1, max = 200, message = "Description must be between 1 and 200 characters")
    String description;

}
