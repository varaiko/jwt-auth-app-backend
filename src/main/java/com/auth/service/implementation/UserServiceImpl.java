package com.auth.service.implementation;

import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.exception.ResourceNotFoundException;
import com.auth.security.JwtGenerator;
import com.auth.service.UserService;
import com.auth.utils.JwtUtils;
import com.auth.utils.RoleUtils;
import com.auth.dto.response.UserResponseDto;
import com.auth.entity.Permission;
import com.auth.exception.InvalidTokenException;
import com.auth.repository.RoleRepository;
import com.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final RoleRepository roleRepository;
    private final JwtGenerator tokenGenerator;
    private final JwtUtils jwtUtils;
    private final RoleUtils roleUtils;

    public UserDetails loadUserDataWithAuthorities(HttpServletRequest request) {

        String token = jwtUtils.extractToken(request);

        if (!StringUtils.hasText(token) || !tokenGenerator.validateToken(token)) {
            log.warn("USER_WARN: Invalid or missing token");
            throw new InvalidTokenException("Invalid or missing token");
        }

        long userId = tokenGenerator.getUserIdFromToken(token);

        User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(() -> {
            log.warn("USER_WARN: No user with ID {}", userId);
            return new UsernameNotFoundException("No user with ID " + userId);
        });

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), mapRoleToAuthorities(user.getRoles()));
    }

    public UserResponseDto loadUserDetailsData(HttpServletRequest request) {

        String token = jwtUtils.extractToken(request);

        if (!StringUtils.hasText(token) || !tokenGenerator.validateToken(token)) {
            log.warn("USER_WARN: Invalid or missing token");
            throw new InvalidTokenException("Invalid or missing token");
        }

        long userId = tokenGenerator.getUserIdFromToken(token);

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("USER_WARN: No user with ID {}", userId);
            return new ResourceNotFoundException("User", "id", userId);
        });

        Role role = user.getRoles();
        List<String> permissions = role.getPermissions().stream().map(Permission::getName).collect(Collectors.toList());

        return new UserResponseDto(user.getId(), user.getUsername(), user.getRoles().getName(), permissions, user.getLastLoginDate());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            log.warn("USER_WARN: No user with username {}", username);
            return new ResourceNotFoundException("User", "id", username);
        });
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), mapRoleToAuthorities(user.getRoles()));
    }

    @Override
    public Page<UserResponseDto> getUserByKeyword(Pageable pageable, String searchInput) {
        Page<User> users = userRepository.findByUsernameContainingIgnoreCase(pageable, searchInput);
        log.info("USER_SEARCH_KEYWORD: keyword {}, page number {}, page size {}", searchInput, pageable.getPageNumber(), pageable.getPageSize());
        return users.map(user -> {
            Role role = user.getRoles();
            String roleName = role.getName();
            List<String> permissions = roleUtils.mapPermissions(role);
            return new UserResponseDto(user.getId(), user.getUsername(), roleName, permissions, user.getLastLoginDate());
        });
    }

    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        Page<User> allUsers = userRepository.findAll(pageable);

        log.info("USER_SEARCH_ALL: page number {} page size {}", pageable.getPageNumber(), pageable.getPageSize());

        return allUsers.map(user -> {
            Role role = user.getRoles();
            String roleName = role.getName();
            List<String> permissions = roleUtils.mapPermissions(role);

            return new UserResponseDto(user.getId(), user.getUsername(), roleName, permissions, user.getLastLoginDate());
        });
    }

    public UserResponseDto getSpecificUser(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            log.warn("USER_SEARCH_WARN: No user with ID {}", id);
            return new ResourceNotFoundException("User", "id", id);
        });

        Role role = user.getRoles();

        List<String> permissions = role.getPermissions().stream().map(Permission::getName).collect(Collectors.toList());

        log.info("USER_SEARCH_SUCCESS: User loaded with ID {}", id);

        return new UserResponseDto(user.getId(), user.getUsername(), user.getRoles().getName(), permissions, user.getLastLoginDate());
    }

    public UserResponseDto updateUser(UserResponseDto userResponseDto) {
        User user = userRepository.findById(userResponseDto.getId()).orElseThrow(() -> {
            log.warn("USER_UPDATE_WARN: No user with ID {}", userResponseDto.getId());
            return new ResourceNotFoundException("User", "id", userResponseDto.getId());
        });

        user.setUsername(userResponseDto.getUsername());

        Role userRole = roleRepository.findByName(userResponseDto.getRole());

        user.setRoles(userRole);

        User updatedUser = userRepository.save(user);

        log.info("USER_UPDATE_SUCCESS: User updated with ID {}", userResponseDto.getId());

        return mapToDto(updatedUser);
    }

    public void deleteUser(long id) {
        userRepository.findById(id).orElseThrow(() -> {
            log.warn("USER_DELETE_WARN: No user with ID {}", id);
            return new ResourceNotFoundException("User", "id", id);
        });
        userRepository.deleteById(id);
        log.info("USER_DELETE_SUCCESS: User deleted with ID {}", id);
    }

    private Collection<? extends GrantedAuthority> mapRoleToAuthorities(Role role) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

        role.getPermissions().forEach(permission ->
            authorities.add(new SimpleGrantedAuthority(permission.getName()))
        );

        return authorities;
    }

    private UserResponseDto mapToDto(User user) {
        return mapper.map(user, UserResponseDto.class);
    }
}
