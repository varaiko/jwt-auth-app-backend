package com.auth.config;

import com.auth.entity.Role;
import com.auth.exception.ResourceNotFoundException;
import com.auth.repository.RoleRepository;
import com.auth.entity.Permission;
import com.auth.repository.PermissionRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Order(1)
public class DefaultRoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public void run(String... args) {

        // Create
        createPermissions("CREATE_STORY", "CREATE_STORY");
        createPermissions("CREATE_COMMENT", "CREATE_COMMENT");
        // Read
        createPermissions("READ_STORY", "READ_STORY");
        createPermissions("READ_COMMENT", "READ_COMMENT");
        // Update
        createPermissions("UPDATE_STORY", "UPDATE_STORY");
        createPermissions("UPDATE_COMMENT", "UPDATE_COMMENT");
        // Delete
        createPermissions("DELETE_STORY", "DELETE_STORY");
        createPermissions("DELETE_COMMENT", "DELETE_COMMENT");
        // Read account
        createPermissions("READ_ACCOUNT", "READ_ACCOUNT");

        Permission CREATE_STORY = permissionRepository.findByName("CREATE_STORY").orElseThrow(() -> new ResourceNotFoundException("Permission", "name", "READ"));
        Permission CREATE_COMMENT = permissionRepository.findByName("CREATE_COMMENT").orElseThrow(() -> new ResourceNotFoundException("Permission", "name", "WRITE"));
        Permission READ_STORY = permissionRepository.findByName("READ_STORY").orElseThrow(() -> new ResourceNotFoundException("Permission", "name", "CHANGE"));
        Permission READ_COMMENT = permissionRepository.findByName("READ_COMMENT").orElseThrow(() -> new ResourceNotFoundException("Permission", "name", "DELETE"));
        Permission UPDATE_STORY = permissionRepository.findByName("UPDATE_STORY").orElseThrow(() -> new ResourceNotFoundException("Permission", "name", "READ"));
        Permission UPDATE_COMMENT = permissionRepository.findByName("UPDATE_COMMENT").orElseThrow(() -> new ResourceNotFoundException("Permission", "name", "WRITE"));
        Permission DELETE_STORY = permissionRepository.findByName("DELETE_STORY").orElseThrow(() -> new ResourceNotFoundException("Permission", "name", "CHANGE"));
        Permission DELETE_COMMENT = permissionRepository.findByName("DELETE_COMMENT").orElseThrow(() -> new ResourceNotFoundException("Permission", "name", "DELETE"));
        Permission READ_ACCOUNT = permissionRepository.findByName("READ_ACCOUNT").orElseThrow(() -> new ResourceNotFoundException("Permission", "name", "READ"));

        if (!roleRepository.existsByName("USER")) {
            Role userRole = new Role();
            userRole.setName("USER");
            userRole.setPermissions(List.of(READ_STORY, READ_COMMENT, CREATE_COMMENT, READ_ACCOUNT));
            roleRepository.save(userRole);
        }

        if (!roleRepository.existsByName("CREATOR")) {
            Role userRole = new Role();
            userRole.setName("CREATOR");
            userRole.setPermissions(List.of(CREATE_STORY, READ_STORY, READ_COMMENT, UPDATE_STORY, READ_ACCOUNT));
            roleRepository.save(userRole);
        }

        if (!roleRepository.existsByName("SUPERADMIN")) {
            Role superadminRole = new Role();
            superadminRole.setName("SUPERADMIN");
            superadminRole.setPermissions(List.of(CREATE_STORY, CREATE_COMMENT, READ_STORY, READ_COMMENT, UPDATE_STORY, UPDATE_COMMENT, DELETE_STORY, DELETE_COMMENT, READ_ACCOUNT));
            roleRepository.save(superadminRole);
        }
    }

    private void createPermissions(String permissionName, String permissionDescription) {
        if (!permissionRepository.existsByName(permissionName)) {
            Permission permission = new Permission();
            permission.setName(permissionName);
            permission.setDescription(permissionDescription);
            permissionRepository.save(permission);
        }
    }
}
