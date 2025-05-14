package com.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleRequestDto {

    private long id;

    @NotBlank(message = "Role name can not be empty")
    @Size(min = 3, max = 25, message = "Role name must be between 3 and 25 characters")
    private String name;

    private List<String> permissions;
}
