package com.auth.service.implementation;

import com.auth.entity.Comment;
import com.auth.entity.Story;
import com.auth.repository.CommentRepository;
import com.auth.repository.StoryRepository;
import com.auth.dto.response.UserResponseDto;
import com.auth.entity.User;
import com.auth.exception.ForbiddenOperationException;
import com.auth.repository.UserRepository;
import com.auth.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.auth.dto.request.CommentRequestDto;
import com.auth.exception.ResourceNotFoundException;
import com.auth.service.CommentService;
import org.modelmapper.ModelMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final StoryRepository storyRepository;
    private final ModelMapper mapper;
    private final UserServiceImpl customUserDetailsService;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @Transactional
    public CommentRequestDto addNewComment(long storyId, CommentRequestDto commentRequestDto) {

        Story story = storyRepository.findById(storyId).orElseThrow(() -> {
            log.error("COMMENT_ERROR: Story not found with ID {}", storyId);
            return new ResourceNotFoundException("Story", "id", storyId);
        });

        User user = userRepository.findByUsername(commentRequestDto.getUsername()).orElseThrow(() -> {
            log.error("COMMENT_ERROR: No user with email {}", commentRequestDto.getUsername());
            return new UsernameNotFoundException("No user with email " + commentRequestDto.getUsername());
        });

        Comment comment = new Comment();

        comment.setComment(commentRequestDto.getComment());
        comment.setStory(story);
        comment.setDate(LocalDateTime.now());
        comment.setUser(user);

        commentRepository.save(comment);

        CommentRequestDto modifiedComment = mapper.map(comment, CommentRequestDto.class);
        modifiedComment.setUsername(user.getUsername());

        log.info("User with ID {} added comment '{}' to story with ID {}", user.getId(), commentRequestDto.getComment(), story.getId());

        return modifiedComment;
    }

    public List<CommentRequestDto> getAllStoryComments(long storyId) {

        storyRepository.findById(storyId).orElseThrow(() -> {
            log.error("COMMENT_ERROR: Story not found with ID {}", storyId);
            return new ResourceNotFoundException("Story", "id", storyId);
        });

        List<Comment> comments = commentRepository.findByStoryId(storyId);

        List<CommentRequestDto> commentsFiltered = comments.stream().map(comment -> {
            CommentRequestDto commentFiltered = mapper.map(comment, CommentRequestDto.class);
            commentFiltered.setUsername(comment.getUser().getUsername());
            return commentFiltered;
        }).collect(Collectors.toList());

        log.info("STORY_ALL_COMMENTS: All story comments loaded for story with ID {}", storyId);

        return commentsFiltered;
    }

    @Transactional
    public CommentRequestDto changeComment(long storyId, long commentId, CommentRequestDto commentRequestDto, HttpServletRequest request) {

        Story story = storyRepository.findById(storyId).orElseThrow(() -> {
            log.error("COMMENT_ERROR: Story not found with ID {}", storyId);
            return new ResourceNotFoundException("Story", "id", storyId);
        });

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            log.error("COMMENT_ERROR: Comment not found with ID {}", commentId);
            return new ResourceNotFoundException("Comment", "id", commentId);
        });

        UserResponseDto userResponseDto = customUserDetailsService.loadUserDetailsData(request);

        if (!comment.getStory().getId().equals(story.getId()) || !comment.getUser().getUsername().equals(userResponseDto.getUsername())) {
            log.error("COMMENT_ERROR: User {} tried to change other user comment with ID {} or the comment did not belong to story {}", comment.getUser().getUsername(), commentId, story.getId());
            throw new ForbiddenOperationException(HttpStatus.FORBIDDEN, "Comment does not belong to story or is not created by the same user");
        }

        comment.setComment(commentRequestDto.getComment());

        commentRepository.save(comment);

        log.info("COMMENT_SUCCESS: User with ID {} changed story with ID {} comment to '{}'", userResponseDto.getId(), story.getId(), commentRequestDto.getComment());

        return mapToDto(comment);

    }

    public void deleteComment(long commentId, HttpServletRequest request) {
        commentRepository.findById(commentId).orElseThrow(() -> {
            log.error("COMMENT_ERROR: Comment not found with ID {}", commentId);
            return new ResourceNotFoundException("Comment", "id", commentId);
        });
        long editorUserId = jwtUtils.getUserIdFromRequest(request);
        commentRepository.deleteById(commentId);
        log.info("Comment with ID {} deleted by user with ID {}", commentId, editorUserId);
    }

    public void deleteAllStoryComments(long storyId, HttpServletRequest request) {
        storyRepository.findById(storyId).orElseThrow(() -> {
            log.error("COMMENT_ERROR: Story not found with ID {}", storyId);
            return new ResourceNotFoundException("Story", "id", storyId);
        });
        List<Comment> comments = commentRepository.findByStoryId(storyId).stream().collect(Collectors.toList());
        long editorUserId = jwtUtils.getUserIdFromRequest(request);
        commentRepository.deleteAllInBatch(comments);
        log.info("All story with ID {} comments were deleted by user with ID {}", storyId, editorUserId);
    }

    private CommentRequestDto mapToDto(Comment comment) {
        return mapper.map(comment, CommentRequestDto.class);
    }
}
