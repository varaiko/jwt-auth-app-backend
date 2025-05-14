package com.auth.service;

import com.auth.dto.response.UserResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService {

    UserDetails loadUserDataWithAuthorities(HttpServletRequest request);

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    UserResponseDto loadUserDetailsData(HttpServletRequest request);

    Page<UserResponseDto> getUserByKeyword(Pageable pageable, String searchInput);

    Page<UserResponseDto> getAllUsers(Pageable pageable);

    UserResponseDto getSpecificUser(long id);

    UserResponseDto updateUser(UserResponseDto userResponseDto);

    void deleteUser(long id);
}
