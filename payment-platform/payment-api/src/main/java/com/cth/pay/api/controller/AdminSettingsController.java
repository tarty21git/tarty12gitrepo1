package com.cth.pay.api.controller;

import com.cth.pay.api.security.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/settings")
@Tag(name = "Admin Settings", description = "Endpoints for configuring dynamic application properties, LDAP authentication, and 2FA settings")
public class AdminSettingsController {

    private final UserService userService;

    public AdminSettingsController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get application and security settings")
    public ResponseEntity<?> getSettings() {
        return ResponseEntity.ok(Map.of(
                "appName", userService.getAppName(),
                "ldapEnabled", userService.isLdapEnabled(),
                "twoFactorEnabled", userService.is2FAEnabled()
        ));
    }

    @PostMapping("/app-name")
    @Operation(summary = "Update Application Name")
    public ResponseEntity<?> setAppName(@RequestParam String appName) {
        userService.setAppName(appName);
        return ResponseEntity.ok(Map.of("message", "Application name updated", "appName", appName));
    }

    @PostMapping("/ldap")
    @Operation(summary = "Enable or disable LDAP authentication")
    public ResponseEntity<?> setLdapEnabled(@RequestParam boolean enabled) {
        userService.setLdapEnabled(enabled);
        return ResponseEntity.ok(Map.of("message", "LDAP authentication set to " + enabled, "ldapEnabled", enabled));
    }

    @PostMapping("/2fa")
    @Operation(summary = "Enable or disable SMS/Email 2FA authentication")
    public ResponseEntity<?> set2FAEnabled(@RequestParam boolean enabled) {
        userService.set2FAEnabled(enabled);
        return ResponseEntity.ok(Map.of("message", "Two-Factor authentication set to " + enabled, "twoFactorEnabled", enabled));
    }
}
