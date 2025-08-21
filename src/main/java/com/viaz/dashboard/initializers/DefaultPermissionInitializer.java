package com.viaz.dashboard.initializers;

import com.viaz.dashboard.entity.permission.PermissionService;
import com.viaz.dashboard.entity.permission.helper.PermissionList;
import com.viaz.dashboard.entity.role.Role;
import com.viaz.dashboard.entity.role.RoleService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.results.graph.Initializer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Component
@RequiredArgsConstructor
public class DefaultPermissionInitializer {

    private final PermissionService permissionService;
    private final RoleService roleService;

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
        addPermissionToRole(adminRole, "USER_MANAGE");
        addPermissionToRole(adminRole, "ROLES_MANAGE");
        adminRole.setSystemProtected(true);
        roleService.saveRole(adminRole);
    }

    private void managerPermission(@NotNull Role managerRole) {
//        User Permissions
        addPermissionToRole(managerRole, "USER_READ");
        addPermissionToRole(managerRole, "USER_WRITE");
        addPermissionToRole(managerRole, "USER_MANAGE");
        addPermissionToRole(managerRole, "ROLES_READ");
        roleService.saveRole(managerRole);
    }

    private void customerServicePermission(@NotNull Role customerServiceRole) {
        addPermissionToRole(customerServiceRole, "USER_READ");
        addPermissionToRole(customerServiceRole, "USER_WRITE");
        roleService.saveRole(customerServiceRole);
    }

    private void assignPermissionsToRoles() {
        // Assign all permissions to ADMIN role
        roleService.findByName("ADMIN").ifPresentOrElse(
                this::adminPermission,
                () -> {
                    Role adminRole = roleService.createRole("ADMIN", "Administrator with full user management capabilities", "Administrator");
                    adminPermission(adminRole);
                }
        );

        // Assign limited permissions to MANAGER role
        roleService.findByName("MANAGER").ifPresentOrElse(
                this::managerPermission,
                () -> {
                    Role managerRole = roleService.createRole("MANAGER", "Manager with user management capabilities", "Manager");
                    managerPermission(managerRole);
                }
        );

        // Assign even less permissions to CUSTOMER_SERVICE role
        roleService.findByName("CUSTOMER_SERVICE").ifPresentOrElse(
                this::customerServicePermission,
//                User Permissions
                () -> {
                    Role customerServiceRole = roleService.createRole("CUSTOMER_SERVICE", "User with basic privileges to manage shipments.", "Manager");
                    customerServicePermission(customerServiceRole);
                }
        );


        // Assign basic permissions to USER role
        roleService.findByName("USER").ifPresentOrElse(
                userRole -> {
                    addPermissionToRole(userRole, "USER_READ");
                    roleService.saveRole(userRole);
                },
                () -> {
                    Role userRole = roleService.createRole("USER", "Basic user with read access", "User");
                    addPermissionToRole(userRole, "USER_READ");
                    roleService.saveRole(userRole);
                }
        );
    }


    private void addPermissionToRole(Role role, String permissionName) {
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

