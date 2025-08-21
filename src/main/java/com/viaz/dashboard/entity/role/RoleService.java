package com.viaz.dashboard.entity.role;


import com.viaz.dashboard.entity.permission.Permission;
import com.viaz.dashboard.entity.permission.PermissionRepository;
import com.viaz.dashboard.entity.permission.PermissionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionService permissionService;

    public Role createRole(String name, String description) {
        if (roleRepository.existsByName(name)) {
            throw new IllegalArgumentException("Role with name " + name + " already exists.");
        }

        Role newRole = new Role(name, description);
        return roleRepository.save(newRole);
    }

    public Role createRole(String name, String description, String displayName) {
        if (roleRepository.existsByName(name)) {
            throw new IllegalArgumentException("Role with name " + name + " already exists.");
        }

        Role newRole = new Role(name, description, displayName);
        return roleRepository.save(newRole);
    }

    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

    public Optional<Role> findRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    @Transactional
    public void deleteRole(Role role) {
        if (role.isSystemProtected()) {
            throw new IllegalStateException("System role 'ADMIN' cannot be deleted");
        }
        roleRepository.delete(role);
    }


    public void addPermissionToRole(String roleName, String permission) {
        Optional<Role> roleOpt = findByName(roleName);
        if (roleOpt.isPresent()) {
            if( roleOpt.get().isSystemProtected()){
                throw new IllegalStateException("System role 'ADMIN' cannot be modified");
            }
            Role role = roleOpt.get();
            Permission permissionToAdd = permissionService.getPermissionByName(permission).orElseThrow(() -> new IllegalArgumentException("Permission not found: " + permission));
            role.addPermission(permissionToAdd);
            saveRole(role);
        }
    }

    public void removePermissionFromRole(String roleName, Permission permission) {
        Optional<Role> roleOpt = findByName(roleName);
        if (roleOpt.isPresent()) {
            if( roleOpt.get().isSystemProtected()){
                throw new IllegalStateException("System role 'ADMIN' cannot be modified");
            }
            Role role = roleOpt.get();
            role.removePermission(permission);
            saveRole(role);
        }
    }


}
