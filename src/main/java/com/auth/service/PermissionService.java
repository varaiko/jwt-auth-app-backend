package com.auth.service;

import com.auth.dto.response.PermissionResponseDto;

import java.util.List;

public interface PermissionService {

    List<PermissionResponseDto> getAllPermissions();
}
