package com.auth.service.implementation;

import com.auth.dto.request.RolePermissionsRequestDto;
import com.auth.dto.request.RoleRequestDto;
import com.auth.entity.Permission;
import com.auth.entity.Role;
import com.auth.exception.ResourceNotFoundException;
import com.auth.repository.RoleRepository;
import com.auth.service.RoleService;
import com.auth.repository.PermissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RoleServiceImplementation implements RoleService {

    private final RoleRepository roleRepository;

    private final PermissionRepository permissionRepository;

    private final ModelMapper mapper;

    public RoleServiceImplementation(RoleRepository roleRepository, ModelMapper mapper, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.mapper = mapper;
        this.permissionRepository = permissionRepository;
    }

    public Page<RoleRequestDto> getAllRoles(Pageable pageable) {
        log.info("ROLE_READ_ALL: page {} size {}", pageable.getPageNumber(), pageable.getPageSize());
        return roleRepository.findAll(pageable).map(this::mapToDto);
    }

    public RoleRequestDto createNewRole(RoleRequestDto roleRequestDto) {
        Role role = new Role();
        role.setName(roleRequestDto.getName());
        Role newRole = roleRepository.save(role);
        log.info("ROLE_CREATE_SUCCESS: New role added with name {}", roleRequestDto.getName());
        return mapToDto(newRole);
    }

    public RoleRequestDto assignNewPermission(long roleId, RolePermissionsRequestDto rolePermissionsRequestDto) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> {
            log.warn("ROLE_WARN: Could not find role with ID {}", roleId);
            return new ResourceNotFoundException("Role", "id", roleId);
        });
        List<Permission> permissions = permissionRepository.findAllById(rolePermissionsRequestDto.getPermissionIds());
        role.setPermissions(permissions);
        Role newRole = roleRepository.save(role);
        log.info("ROLE_ASSIGN_PERM_SUCCESS: New permissions assigned to {} role", newRole.getName());
        return mapToDto(newRole);
    }

    public RoleRequestDto getRoleById(long roleId) {

        Role role = roleRepository.findById(roleId).orElseThrow(() -> {
            log.warn("ROLE_WARN: Could not find role with ID {}", roleId);
            return new ResourceNotFoundException("Role", "id", roleId);
        });

        List<String> permissions = role.getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toList());

        log.info("ROLE_SEARCH: Role with ID {}", roleId);

        return new RoleRequestDto(role.getId(), role.getName(), permissions);
    }

    @Override
    public Page<RoleRequestDto> getRolesByKeyword(Pageable pageable, String keyword) {
        return roleRepository.findByNameContainingIgnoreCase(pageable, keyword).map(this::mapToDto);
    }

    // Convert Entity to DTO
    private RoleRequestDto mapToDto(Role role) {
        return mapper.map(role, RoleRequestDto.class);
    }

}
