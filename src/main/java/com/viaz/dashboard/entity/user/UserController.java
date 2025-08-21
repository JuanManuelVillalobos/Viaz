package com.viaz.dashboard.entity.user;

import com.viaz.dashboard.entity.user.helpers.ViazUserDTO;
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

    @GetMapping("/get-all-users")
    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'USER_READ')")
    public ResponseEntity<List<ViazUserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/delete-user/{userId}")
    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'USER_DELETE')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/unlock")
    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'USER_WRITE')")
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

    @DeleteMapping("/{userId}/remove-role/{roleName}")
    @PreAuthorize("@permissionEvaluatorImpl.hasPermission(authentication, 'ROLES_DELETE')")
    public ResponseEntity<Void> removeRole(@PathVariable Long userId, @PathVariable String roleName) {
        userService.removeRoleFromUser(userId, roleName);
        return ResponseEntity.ok().build();

    }

}