package com.auth.service;

import com.auth.dto.request.StoryRequestDto;
import com.auth.dto.response.StoryResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoryService {

    StoryRequestDto createStory(StoryRequestDto storyRequestDto);

    Page<StoryResponseDto> getAllStories(Pageable pageable);

    StoryResponseDto getStoryById(long id);

    StoryRequestDto changeStory(StoryRequestDto storyRequestDto, long id, HttpServletRequest request);

    Page<StoryResponseDto> getStoryByKeyword(Pageable pageable, String searchInput);

    void deleteStory(long id, HttpServletRequest request);
}