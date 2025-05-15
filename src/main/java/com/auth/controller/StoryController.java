package com.auth.controller;

import com.auth.dto.response.StoryResponseDto;
import com.auth.dto.request.StoryRequestDto;
import com.auth.service.StoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stories")
@AllArgsConstructor

public class StoryController {

    private final StoryService storyService;

    @PostMapping("")
    @PreAuthorize("hasPermission(#storyRequestDto, 'CREATE_STORY')")
    public ResponseEntity<StoryRequestDto> postStory(@RequestBody StoryRequestDto storyRequestDto) {
        return new ResponseEntity<>(storyService.createStory(storyRequestDto), HttpStatus.CREATED);
    }

    @GetMapping("")
    @PreAuthorize("hasPermission(filterObject, 'READ_STORY')")
    public ResponseEntity<Page<StoryResponseDto>> getAllStories(Pageable pageable) {
        return ResponseEntity.ok(storyService.getAllStories(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(filterObject, 'READ_STORY')")
    public ResponseEntity<StoryResponseDto> getStoryById(@PathVariable long id) {
        return ResponseEntity.ok(storyService.getStoryById(id));
    }

    @GetMapping("/search")
    @PreAuthorize("hasPermission(filterObject, 'READ_STORY')")
    public ResponseEntity<Page<StoryResponseDto>> getStoriesByKeyword(Pageable pageable, @RequestParam String keyword) {
        return ResponseEntity.ok(storyService.getStoryByKeyword(pageable, keyword));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(#storyRequestDto, 'UPDATE_STORY')")
    public ResponseEntity<StoryRequestDto> changeStory(@PathVariable long id, @RequestBody StoryRequestDto storyRequestDto, HttpServletRequest request) {
        return ResponseEntity.ok(storyService.changeStory(storyRequestDto, id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'DELETE_STORY') || hasRole('SUPERADMIN')")
    public ResponseEntity<String> deleteStory(@PathVariable long id, HttpServletRequest request) {
        storyService.deleteStory(id, request);
        return ResponseEntity.noContent().build();
    }
}
