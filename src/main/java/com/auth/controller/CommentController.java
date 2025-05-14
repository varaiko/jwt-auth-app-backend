package com.auth.controller;

import com.auth.dto.request.CommentRequestDto;
import com.auth.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/stories/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/addcomment/{storyId}")
    @PreAuthorize("hasPermission(#storyId, 'CREATE_COMMENT')")
    public ResponseEntity<CommentRequestDto> createComment(@PathVariable long storyId, @Valid @RequestBody CommentRequestDto commentRequestDto) {
        return new ResponseEntity<>(commentService.addNewComment(storyId, commentRequestDto), HttpStatus.CREATED);
    }

    @GetMapping("/{storyId}")
    @PreAuthorize("hasPermission(#storyId, 'READ_COMMENT')")
    public List<CommentRequestDto> getStoryComments(@PathVariable long storyId) {
        return commentService.getAllStoryComments(storyId);
    }

    @PutMapping("/{storyId}/{commentId}")
    @PreAuthorize("hasPermission(#commentId, 'UPDATE_COMMENT')")
    public ResponseEntity<CommentRequestDto> updateComment(@PathVariable long storyId, @PathVariable long commentId, @Valid @RequestBody CommentRequestDto commentRequestDto, HttpServletRequest request) {
        return new ResponseEntity<>(commentService.changeComment(storyId, commentId, commentRequestDto, request), HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasPermission(#commentId, 'DELETE_COMMENT')")
    public ResponseEntity<String> deleteComment(@PathVariable long commentId, HttpServletRequest request) {
        commentService.deleteComment(commentId, request);
        return ResponseEntity.noContent().build();
    }

}
