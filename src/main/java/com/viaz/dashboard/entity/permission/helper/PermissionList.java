package com.viaz.dashboard.entity.permission.helper;

import lombok.Getter;

@Getter
public enum PermissionList {
    USER_READ("USER_READ", "Read user information", "USER", "READ"),
    USER_WRITE("USER_WRITE", "Create and update users", "USER", "WRITE"),
    USER_DELETE("USER_DELETE", "Delete users", "USER", "DELETE"),
    USER_MANAGE("USER_MANAGE", "Full user management", "USER", "MANAGE"),
    ROLES_READ("ROLES_READ", "Read role information", "ROLE", "READ"),
    ROLES_WRITE("ROLES_WRITE", "Create and update roles", "ROLE", "WRITE"),
    ROLES_DELETE("ROLES_DELETE", "Delete roles", "ROLE", "DELETE"),
    ROLES_ASSIGN("ROLES_ASSIGN", "Assign/remove roles", "ROLE", "ASSIGN"),
    ROLES_MANAGE("ROLES_MANAGE", "Full role management", "ROLE", "MANAGE");

    // Getters
    private final String name;
    private final String description;
    private final String resource;
    private final String action;

    PermissionList(String name, String description, String resource, String action) {
        this.name = name;
        this.description = description;
        this.resource = resource;
        this.action = action;
    }

}

