package com.viaz.dashboard.entity.role;

import com.viaz.dashboard.entity.role.helpers.RoleCreationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "api/role")
public class RoleController {


    private final RoleService roleService;


    @PostMapping("/create-role")
    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'ROLE_WRITE')") // Only roles with ROLES_WRITE permission can create roles
    public ResponseEntity<Role> createRole(@RequestBody RoleCreationRequest request) {
        try {
            // Sanitize input to prevent XSS
            String sanitizedName = HtmlUtils.htmlEscape(request.name());
            String sanitizedDescription = HtmlUtils.htmlEscape(request.description());

            Role newRole = roleService.createRole(sanitizedName, sanitizedDescription);
            return new ResponseEntity<>(newRole, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT); // Or specific error response
        }
    }


    @GetMapping("/get-all-roles")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication,'ROLE_READ')")
    public List<Role> getAllRoles() {
        return (roleService.getAllRoles());
    }


    @PostMapping("/add-permision-to-role/{roleName}/{permision}")
    @PreAuthorize("@permissionEvaluator.hasAllPermissions(authentication, 'ROLE_WRITE', 'PERMISSIONS_WRITE')")
    public ResponseEntity<Map<String,Object>> addPermissionToRole(@PathVariable String roleName, @PathVariable String permision) {
        roleService.addPermissionToRole(roleName, permision);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Permission added successfully to role " + roleName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
