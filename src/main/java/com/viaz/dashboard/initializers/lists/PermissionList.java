package com.viaz.dashboard.initializers.lists;

import lombok.Getter;


@Getter
public enum PermissionList {
    ADMIN_OVERRIDE("ADMIN_OVERRIDE", "Override all permissions", "ADMIN", "OVERRIDE"),


    SHIPMENT_READ("SHIPMENT_READ", "Read shipment information", "SHIPMENT", "READ"),
    SHIPMENT_WRITE("SHIPMENT_WRITE", "Create and update shipments", "SHIPMENT", "WRITE"),
    SHIPMENT_DELETE("SHIPMENT_DELETE", "Delete shipments", "SHIPMENT", "DELETE"),
    SHIPMENT_MANAGE("SHIPMENT_MANAGE", "Full shipment management", "SHIPMENT", "MANAGE"),
    SHIPMENT_CLOSE("SHIPMENT_CLOSE", "Ability to closea and rate shipments", "SHIPMENT", "CLOSE"),


    USER_READ("USER_READ", "Read user information", "USER", "READ"),
    USER_WRITE("USER_WRITE", "Create and update users", "USER", "WRITE"),
    USER_DELETE("USER_DELETE", "Delete users", "USER", "DELETE"),
    USER_MANAGE("USER_MANAGE", "Full user management", "USER", "MANAGE"),


    ROLES_READ("ROLES_READ", "Read role information", "ROLES", "READ"),
    ROLES_WRITE("ROLES_WRITE", "Create and update roles", "ROLES", "WRITE"),
    ROLES_DELETE("ROLES_DELETE", "Delete roles", "ROLES", "DELETE"),
    ROLES_ASSIGN("ROLES_ASSIGN", "Assign/remove roles", "ROLES", "ASSIGN"),
    ROLES_MANAGE("ROLES_MANAGE", "Full role management", "ROLES", "MANAGE"),

    CARRIER_READ("CARRIER_READ", "Read carrier information", "CARRIER", "READ"),
    CARRIER_WRITE("CARRIER_WRITE", "Create and update carriers", "CARRIER", "WRITE"),
    CARRIER_DELETE("CARRIER_DELETE", "Delete carriers", "CARRIER", "DELETE"),
    CARRIER_MANAGE("CARRIER_MANAGE", "Full carrier management", "CARRIER", "MANAGE");
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

