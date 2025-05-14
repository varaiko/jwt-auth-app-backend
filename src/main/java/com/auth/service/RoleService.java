package com.auth.service;

import com.auth.dto.request.RolePermissionsRequestDto;
import com.auth.dto.request.RoleRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleService {

    Page<RoleRequestDto> getAllRoles(Pageable pageable);

    RoleRequestDto createNewRole(RoleRequestDto roleRequestDto);

    RoleRequestDto assignNewPermission(long roleId, RolePermissionsRequestDto rolePermissionsRequestDto);

    RoleRequestDto getRoleById(long roleId);

    Page<RoleRequestDto> getRolesByKeyword(Pageable pageable, String keyword);
}
