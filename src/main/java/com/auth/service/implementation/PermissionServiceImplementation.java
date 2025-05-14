package com.auth.service.implementation;

import com.auth.dto.response.PermissionResponseDto;
import com.auth.entity.Permission;
import com.auth.repository.PermissionRepository;
import com.auth.service.PermissionService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PermissionServiceImplementation implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final ModelMapper mapper;

    public List<PermissionResponseDto> getAllPermissions() {
        return permissionRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private PermissionResponseDto mapToDto(Permission permission) {
        return mapper.map(permission, PermissionResponseDto.class);
    }

}
