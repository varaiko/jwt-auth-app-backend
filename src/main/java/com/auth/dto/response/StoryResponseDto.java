package com.auth.dto.response;

import com.auth.dto.request.CommentRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoryResponseDto {

    private long id;
    private String username;
    private String title;
    private String content;
    private LocalDateTime date;
    private String url;
    private List<CommentRequestDto> comments;

}
