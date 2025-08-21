package com.viaz.dashboard.entity.permission;


import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping(path = "api/permissions")
public class PermissionController {


    private final PermissionService permissionService;

    @GetMapping("/get-permissions")
    @PreAuthorize("@permissionEvaluator.hasAnyPermission(authentication, 'ROLE_ASSIGN', 'ROLE_WRITE')")
    public String getPermissions(){
        return permissionService.getAllPermissions().toString();
    }


}
