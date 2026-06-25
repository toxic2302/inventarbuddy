package de.toxic2302.inventarbuddy.core.modules.api.controller;

import de.toxic2302.inventarbuddy.base.authentication.AuthenticatedUserService;
import de.toxic2302.inventarbuddy.core.modules.user.dto.UserDto;
import de.toxic2302.inventarbuddy.core.modules.user.entity.User;
import de.toxic2302.inventarbuddy.core.modules.user.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {

    private final UserService userservice;
    private final AuthenticatedUserService authenticatedUserService;

    public TestController(UserService userservice, AuthenticatedUserService authenticatedUserService) {
        this.userservice = userservice;
        this.authenticatedUserService = authenticatedUserService;
    }

    @GetMapping("/public/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }

    @GetMapping("/me")
    public User getCurrentUser() {
        return authenticatedUserService.getCurrentUser();
    }

    @GetMapping("/admin/users")
    public List<UserDto> listUsers() {
        return userservice.listUsers();
    }
}
