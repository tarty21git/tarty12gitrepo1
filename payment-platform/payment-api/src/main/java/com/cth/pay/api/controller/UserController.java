package com.cth.pay.api.controller;

import com.cth.pay.api.dto.PasswordResetDto;
import com.cth.pay.api.dto.UserRegistrationDto;
import com.cth.pay.api.entity.UserEntity;
import com.cth.pay.api.security.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Endpoints for user registration, locking, password reset, and 2FA settings")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user account")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDto dto) {
        try {
            UserEntity user = userService.registerUser(dto);
            return ResponseEntity.ok(Map.of("message", "User registered successfully", "username", user.getUsername()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Get list of all registered users")
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/{username}/lock")
    @Operation(summary = "Lock or unlock a user account")
    public ResponseEntity<?> setLockStatus(@PathVariable String username, @RequestParam boolean locked) {
        try {
            UserEntity user = userService.setUserLockStatus(username, locked);
            return ResponseEntity.ok(Map.of("message", "User lock status updated", "username", username, "locked", locked));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password for a user account")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetDto dto) {
        try {
            userService.resetPassword(dto);
            return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
