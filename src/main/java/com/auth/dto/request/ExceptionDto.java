package com.auth.dto.request;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExceptionDto {

    private LocalDateTime timestamp;
    private String message;
    private String details;
    private int status;
    private String error;

}
