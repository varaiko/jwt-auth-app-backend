package com.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {

    private long id;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Comment can not be empty")
    @Size(max = 1000, message = "Comment must be less than 1000 characters")
    private String comment;

    @NotNull(message = "Date can not be null")
    private LocalDateTime date;

}
