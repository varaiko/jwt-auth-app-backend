package com.auth.dto.request;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPasswordResetRequestDto {

    long id;

    @NotBlank(message = "Email can not be empty")
    @Email(message = "Email must be valid")
    String email;

    @NotNull(message = "Date can not be empty")
    LocalDateTime expiryTime;

    @NotBlank(message = "Token can not be empty")
    String token;

}
