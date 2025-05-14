package com.auth.controller;

import com.auth.dto.response.PermissionResponseDto;
import com.auth.service.PermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping("")
    public ResponseEntity<List<PermissionResponseDto>> getAllPermission() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

}
