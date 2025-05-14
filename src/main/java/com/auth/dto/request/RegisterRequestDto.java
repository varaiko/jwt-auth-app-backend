package com.auth.dto.request;
import lombok.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder



public class RegisterRequestDto {

    @NotBlank(message = "Username must not be empty")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    String username;

    @NotBlank(message = "Password must not be empty")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    String password;

}
