package com.viaz.dashboard.user;

import com.viaz.dashboard.user.helpers.ViazUserDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Get all users using Data Transfer Object
    @GetMapping("/get-all-users")
    public List<ViazUserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping(path = "/delete-user/{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
    }
}
