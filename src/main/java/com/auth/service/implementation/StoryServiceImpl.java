package com.auth.service.implementation;

import com.auth.dto.response.StoryResponseDto;
import com.auth.dto.request.StoryRequestDto;
import com.auth.entity.Story;
import com.auth.entity.User;
import com.auth.exception.ResourceNotFoundException;
import com.auth.repository.StoryRepository;
import com.auth.repository.UserRepository;
import com.auth.service.CommentService;
import com.auth.service.StoryService;
import com.auth.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class StoryServiceImpl implements StoryService {

    private final StoryRepository storyRepository;
    private final ModelMapper mapper;
    private final CommentService commentService;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @Transactional
    public StoryRequestDto createStory(StoryRequestDto storyRequestDto) {
        User user = userRepository.findByUsername(storyRequestDto.getUsername()).orElseThrow(() -> {
            log.warn("STORY_ERROR: No user with username {}", storyRequestDto.getUsername());
            return new UsernameNotFoundException("No user with email " + storyRequestDto.getUsername());
        });

        Story story = new Story();

        story.setUser(user);
        story.setTitle(storyRequestDto.getTitle());
        story.setDate(storyRequestDto.getDate());
        story.setContent(storyRequestDto.getContent());
        story.setUrl(storyRequestDto.getUrl());

        Story newStory = storyRepository.save(story);

        log.info("STORY_CREATE_SUCCESS: New story created by user {} with ID {}. Story title '{}'", user.getUsername(), newStory.getId(), storyRequestDto.getTitle());

        return mapToDto(newStory);
    }

    @Transactional
    public Page<StoryResponseDto> getAllStories(Pageable pageable) {
        log.info("STORY_READ_ALL: page {} size {}", pageable.getPageNumber(), pageable.getPageSize());
        return storyRepository.findAll(pageable).map(this::mapStoryDataToDto);
    }

    @Transactional
    public StoryResponseDto getStoryById(long id) {
        Story story = storyRepository.findById(id).orElseThrow(() -> {
            log.warn("STORY_READ_ERROR: Could not find story with ID {}", id);
            return new ResourceNotFoundException("Story", "id", id);
        });
        log.info("STORY_READ_SINGULAR: {}", id);
        return mapStoryDataToDto(story);
    }

    @Transactional
    public Page<StoryResponseDto> getStoryByKeyword(Pageable pageable, String searchInput) {
        log.info("STORY_READ_KEYWORD: keyword {} page {} size {}", searchInput, pageable.getPageNumber(), pageable.getPageSize());
        return storyRepository.findByTitleContainingIgnoreCase(pageable, searchInput).map(this::mapStoryDataToDto);
    }

    @Transactional
    public StoryRequestDto changeStory(StoryRequestDto storyRequestDto, long id, HttpServletRequest request) {

        Story story = storyRepository.findById(id).orElseThrow(() -> {
            log.warn("STORY_CHANGE_WARN: No story with id {}", id);
            return new ResourceNotFoundException("Story", "id", id);
        });

        story.setContent(storyRequestDto.getContent());
        story.setTitle(storyRequestDto.getTitle());
        story.setUrl(storyRequestDto.getUrl());

        Story updatedStory = storyRepository.save(story);

        long editorUserId = jwtUtils.getUserIdFromRequest(request);

        log.info("STORY_CHANGE_SUCCESS: Story with ID {} has been changed by user with ID {}", storyRequestDto.getId(), editorUserId);

        return mapToDto(updatedStory);
    }

    @Transactional
    public void deleteStory(long id, HttpServletRequest request) {
        storyRepository.findById(id).orElseThrow(() -> {
            log.warn("STORY_DELETE_WARN: No story with ID {}", id);
            return new ResourceNotFoundException("Story", "id", id);
        });
        long editorUserId = jwtUtils.getUserIdFromRequest(request);
        commentService.deleteAllStoryComments(id, request);
        storyRepository.deleteById(id);
        log.info("STORY_DELETE_SUCCESS: Story with ID {} has been deleted by user with ID {}", id, editorUserId);
    }

    // Convert Entity to DTO
    private StoryRequestDto mapToDto(Story story) {
        return mapper.map(story, StoryRequestDto.class);
    }

    private StoryResponseDto mapStoryDataToDto(Story story) {
        StoryResponseDto dto = new StoryResponseDto();
        dto.setId(story.getId());
        dto.setTitle(story.getTitle());
        dto.setContent(story.getContent());
        dto.setDate(story.getDate());
        dto.setUsername(story.getUser().getUsername());
        dto.setUrl(story.getUrl());
        return dto;
    }

}
