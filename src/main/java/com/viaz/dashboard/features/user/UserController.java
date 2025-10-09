package com.viaz.dashboard.features.user;

import com.viaz.dashboard.features.role.RoleService;
import com.viaz.dashboard.features.user.dto.UserSummaryDTO;
import com.viaz.dashboard.features.user.dto.ViazUserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "api/user")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'USER_READ')")
    @GetMapping("/get-all-users")
    public ResponseEntity<List<ViazUserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'USER_DELETE')")
    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'USER_WRITE')")
    @PostMapping("/{userId}/unlock")
    public ResponseEntity<Void> unlockAccount(@PathVariable Long userId) {
        userService.unlockAccount(userId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'ROLES_ASSIGN')")
    @PostMapping("/{userId}/assign-role/{roleName}")
    public ResponseEntity<Map<String, Object>> assignRole(@PathVariable Long userId, @PathVariable String roleName) {
        Map<String, Object> response = userService.assignRoleToUser(userId, roleName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'ROLES_DELETE')")
    @DeleteMapping("/{userId}/remove-role/{roleName}")
    public ResponseEntity<Map<String, Object>> removeRole(@PathVariable Long userId, @PathVariable String roleName) {
        Map<String, Object> response = userService.removeRoleFromUser(userId, roleName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'SHIPMENT_WRITE')")
    @GetMapping("/get-customer-service-users")
    public ResponseEntity<List<UserSummaryDTO>> getCustomerServiceUsers() {
        List<UserSummaryDTO> response = userService.getUsersByRole("Client Service");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}