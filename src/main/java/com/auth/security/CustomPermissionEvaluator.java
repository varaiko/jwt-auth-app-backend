package com.auth.security;

import com.auth.dto.request.RoleRequestDto;
import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.repository.UserRepository;
import com.auth.service.RoleService;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final RoleService roleService;

    private final UserRepository userRepository;

    public CustomPermissionEvaluator(RoleService roleService, UserRepository userRepository) {
        this.roleService = roleService;
        this.userRepository = userRepository;
    }

    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("No user with email " + userDetails.getUsername()));

        Role roles = user.getRoles();

        RoleRequestDto roleRequestDto = roleService.getRoleById(roles.getId());

        return roleRequestDto.getPermissions().contains(permission.toString());
    }

    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
