package com.viaz.dashboard.initializers.lists;

import lombok.Getter;


@Getter
public enum PermissionList {
    ADMIN_OVERRIDE("ADMIN_OVERRIDE", "Override all permissions", "ADMIN", "OVERRIDE"),


    SHIPMENT_READ("SHIPMENT_READ", "Read shipment information", "SHIPMENT", "READ"),
    SHIPMENT_WRITE("SHIPMENT_WRITE", "Create and update shipments", "SHIPMENT", "WRITE"),
    SHIPMENT_DELETE("SHIPMENT_DELETE", "Delete shipments", "SHIPMENT", "DELETE"),
    SHIPMENT_RESTORE("SHIPMENT_RESTORE", "Restore shipments", "SHIPMENT", "RESTORE"),
    SHIPMENT_MANAGE("SHIPMENT_MANAGE", "Full shipment management", "SHIPMENT", "MANAGE"),
    SHIPMENT_EXPORT("SHIPMENT_EXPORT", "Export shipments to xls file", "SHIPMENT", "EXPORT"),
    SHIPMENT_CLOSE("SHIPMENT_CLOSE", "Ability to closea and rate shipments", "SHIPMENT", "CLOSE"),
    SHIPMENT_INCIDENT_DELETE("SHIPMENT_INCIDENT_DELETE", "Ability to close incidents", "SHIPMENT", "INCIDENT_DELETE"),
    SHIPMENT_CHARGE_DELETE("SHIPMENT_CHARGE_DELETE", "Ability to close charges", "SHIPMENT", "CHARGE_DELETE"),


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
    CARRIER_MANAGE("CARRIER_MANAGE", "Full carrier management", "CARRIER", "MANAGE"),

    CLIENT_READ("CLIENT_READ", "Read client information", "CLIENT", "READ"),
    CLIENT_WRITE("CLIENT_WRITE", "Create and update clients", "CLIENT", "WRITE"),
    CLIENT_DELETE("CLIENT_DELETE", "Delete clients", "CLIENT", "DELETE"),
    CLIENT_MANAGE("CLIENT_MANAGE", "Full client management", "CLIENT", "MANAGE"),

    INCIDENT_MANAGE("INCIDENT_MANAGE", "Full incident management", "INCIDENT", "MANAGE"),
    CHARGE_MANAGE("CHARGE_MANAGE", "Full charge management", "CHARGE", "MANAGE");
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

