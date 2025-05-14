package com.auth.service;

import com.auth.dto.request.CommentRequestDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface CommentService {

    CommentRequestDto addNewComment(long storyId, CommentRequestDto commentRequestDto);

    List<CommentRequestDto> getAllStoryComments(long storyId);

    CommentRequestDto changeComment(long storyId, long commentId, CommentRequestDto commentRequestDto, HttpServletRequest request);

    void deleteComment(long commentid, HttpServletRequest request);
    void deleteAllStoryComments(long storyId, HttpServletRequest request);
}
