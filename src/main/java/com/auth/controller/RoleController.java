package com.auth.controller;

import com.auth.dto.request.RoleRequestDto;
import com.auth.dto.request.RolePermissionsRequestDto;
import com.auth.service.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Page<RoleRequestDto>> getAllRoles(Pageable pageable) {
        Page<RoleRequestDto> roles = roleService.getAllRoles(pageable);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/search-role")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Page<RoleRequestDto>> getRolesByKeyword(Pageable pageable, String keyword) {
        Page<RoleRequestDto> roles = roleService.getRolesByKeyword(pageable, keyword);
        return ResponseEntity.ok(roles);
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<RoleRequestDto> createNewRole(@Valid @RequestBody RoleRequestDto roleRequestDto) {
        return new ResponseEntity<>(roleService.createNewRole(roleRequestDto), HttpStatus.CREATED);
    }

    @PostMapping("/{roleId}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<RoleRequestDto> assignNewPermissionToRole(@PathVariable long roleId, @Valid @RequestBody RolePermissionsRequestDto rolePermissionsRequestDto) {
        return new ResponseEntity<>(roleService.assignNewPermission(roleId, rolePermissionsRequestDto), HttpStatus.OK);
    }

    @GetMapping("/{roleId}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<RoleRequestDto> getRoleById(@PathVariable long roleId) {
        return new ResponseEntity<>(roleService.getRoleById(roleId), HttpStatus.OK);
    }

}
