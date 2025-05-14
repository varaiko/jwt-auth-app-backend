package com.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordRequestDto {

    @NotBlank(message = "Email can not be empty")
    @Email(message = "Email should be valid")
    private String email;

}
