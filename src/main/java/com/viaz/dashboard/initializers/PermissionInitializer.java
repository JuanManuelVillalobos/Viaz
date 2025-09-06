package com.viaz.dashboard.initializers;

import com.viaz.dashboard.entity.permission.PermissionService;
import com.viaz.dashboard.entity.role.Role;
import com.viaz.dashboard.entity.role.RoleService;
import com.viaz.dashboard.entity.role.helpers.RoleCreationRequestDTO;
import com.viaz.dashboard.entity.shipment.idGenerator.ShipmentIdGeneratorService;
import com.viaz.dashboard.initializers.lists.PermissionList;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;

@Order(1)
@Component
@RequiredArgsConstructor
public class PermissionInitializer {

    private final PermissionService permissionService;
    private final RoleService roleService;
    private final ShipmentIdGeneratorService shipmentIdGeneratorService;

    @PostConstruct
    public void initializeDefaultPermissions() {
        createDefaultPermissions();
        assignPermissionsToRoles();
    }

    private void createDefaultPermissions() {
        for (PermissionList perm : PermissionList.values()) {
            createPermissionIfNotExists(perm.getName(), perm.getDescription(), perm.getResource(), perm.getAction());
        }
    }

    private void createPermissionIfNotExists(String name, String description, String resource, String action) {
        if (permissionService.getPermissionByName(name).isEmpty()) {
            permissionService.createPermission(name, description, resource, action);
        }
    }

    private void adminPermission(@NotNull Role adminRole) {
//        User Permissions
        addPermissionToRoleIfNotExists(adminRole, "USER_MANAGE");
        addPermissionToRoleIfNotExists(adminRole, "ROLES_MANAGE");
        addPermissionToRoleIfNotExists(adminRole, "SHIPMENT_MANAGE");
        adminRole.setSystemProtected(true);
        roleService.saveRole(adminRole);
    }


    private void assignPermissionsToRoles() {
        // Assign all permissions to ADMIN role
        roleService.findByName("ADMIN").ifPresentOrElse(
                this::adminPermission,
                () -> {
                    Role adminRole = roleService.createRole(new RoleCreationRequestDTO(
                            "ADMIN",
                            "Administrator with full user management capabilities",
                            Set.of("ADMIN_OVERRIDE")));
                    adminRole.setSystemProtected(true);
                    roleService.saveRole(adminRole);
                }
        );


        // Create cosmetic role for customer service
        if (roleService.findByName("Customer Service").isEmpty()) {
            roleService.createRole(new RoleCreationRequestDTO(
                    "Customer Service",
                    "Cosmetic role for customer service",
                    true
            ));
        }


        // Assign basic permissions to USER role
        roleService.findByName("User").ifPresentOrElse(
                userRole -> {
                    addPermissionToRoleIfNotExists(userRole, "SHIPMENT_READ");
                    roleService.saveRole(userRole);
                },
                () -> {
                    roleService.createRole(new RoleCreationRequestDTO("User", "Basic user with read access", Set.of("SHIPMENT_READ")));
                }
        );
    }


    private void addPermissionToRoleIfNotExists(Role role, String permissionName) {
        if (!role.hasPermission(permissionName)) {
            permissionService.getPermissionByName(permissionName).ifPresent(permission -> {
                if (!role.getPermissions().contains(permission)) {
                    role.addPermission(permission);
                } else {
                    System.out.println("ROLE ALREADY HAS PERMISSION: " + permissionName);
                    throw new IllegalStateException("Role " + role.getName() + " already has permission " + permissionName);
                }
            });
        }
    }

}

