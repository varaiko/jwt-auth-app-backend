package com.auth.utils;

import com.auth.entity.Role;
import com.auth.entity.Permission;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoleUtils {

    public List<String> mapPermissions(Role role) {
        return role.getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toList());
    }

}
