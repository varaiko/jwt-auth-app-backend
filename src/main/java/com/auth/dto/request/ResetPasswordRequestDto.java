package com.auth.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordRequestDto {

    @NotBlank(message = "Token can not be empty")
    String token;

    @NotBlank(message = "New password can not be empty")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    String newPassword;
}
