package com.auth.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyResetTokenRequestDto {

    @NotBlank(message = "Token can not be empty")
    String token;

}
