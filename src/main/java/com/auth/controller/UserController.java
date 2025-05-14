package com.auth.controller;

import com.auth.dto.response.UserResponseDto;
import com.auth.service.implementation.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")

public class UserController {

    private final UserServiceImpl customUserDetailsService;

    public UserController(UserServiceImpl customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @GetMapping("/all-users")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(customUserDetailsService.getAllUsers(pageable));
    }

    @GetMapping("/search-user")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Page<UserResponseDto>> getUserBySearch(Pageable pageable, @RequestParam String keyword) {
        return ResponseEntity.ok(customUserDetailsService.getUserByKeyword(pageable, keyword));
    }

    @GetMapping("/user-info/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<UserResponseDto> getUserInfoById(@PathVariable long id) {
        return ResponseEntity.ok(customUserDetailsService.getSpecificUser(id));
    }

    @GetMapping("/user-info")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> getUserInfoWithToken(HttpServletRequest request) {
        return ResponseEntity.ok(customUserDetailsService.loadUserDetailsData(request));
    }

    @DeleteMapping("/delete-user/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        customUserDetailsService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update-user")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<UserResponseDto> updateUserData(@RequestBody UserResponseDto userResponseDto) {
        return new ResponseEntity<>(customUserDetailsService.updateUser(userResponseDto), HttpStatus.OK);
    }
}
