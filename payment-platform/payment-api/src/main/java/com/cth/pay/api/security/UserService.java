package com.cth.pay.api.security;

import com.cth.pay.api.dto.PasswordResetDto;
import com.cth.pay.api.dto.UserRegistrationDto;
import com.cth.pay.api.entity.SystemSettingEntity;
import com.cth.pay.api.entity.UserEntity;
import com.cth.pay.api.repository.SystemSettingRepository;
import com.cth.pay.api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SystemSettingRepository settingRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       SystemSettingRepository settingRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.settingRepository = settingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserEntity registerUser(UserRegistrationDto dto) {
        if (dto.getUsername() == null || dto.getUsername().trim().length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters long");
        }
        if (dto.getPassword() == null || dto.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username '" + dto.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email '" + dto.getEmail() + "' is already registered");
        }

        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        UserEntity user = new UserEntity(
                dto.getUsername(), encodedPassword, dto.getEmail(),
                dto.getFullName(), dto.getPhoneNumber(),
                dto.getRole() != null ? dto.getRole() : "ROLE_USER"
        );

        UserEntity saved = userRepository.save(user);

        if (is2FAEnabled()) {
            sendAuthenticationNotification(saved, "Account created successfully. Your 2FA code is 123456 (Demo mode)");
        }

        return saved;
    }

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public UserEntity setUserLockStatus(String username, boolean locked) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        user.setAccountNonLocked(!locked);
        return userRepository.save(user);
    }

    @Transactional
    public void resetPassword(PasswordResetDto dto) {
        UserEntity user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + dto.getUsername()));

        if (dto.getNewPassword() == null || dto.getNewPassword().length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setPasswordExpiryDate(LocalDateTime.now().plusDays(90));
        userRepository.save(user);
    }

    public boolean isLdapEnabled() {
        return settingRepository.findById("auth.ldap.enabled")
                .map(s -> Boolean.parseBoolean(s.getSettingValue()))
                .orElse(false);
    }

    @Transactional
    public void setLdapEnabled(boolean enabled) {
        SystemSettingEntity setting = settingRepository.findById("auth.ldap.enabled")
                .orElse(new SystemSettingEntity("auth.ldap.enabled", "false", "Enable Windows Active Directory / LDAP authentication"));
        setting.setSettingValue(String.valueOf(enabled));
        settingRepository.save(setting);
    }

    public boolean is2FAEnabled() {
        return settingRepository.findById("auth.2fa.enabled")
                .map(s -> Boolean.parseBoolean(s.getSettingValue()))
                .orElse(false);
    }

    @Transactional
    public void set2FAEnabled(boolean enabled) {
        SystemSettingEntity setting = settingRepository.findById("auth.2fa.enabled")
                .orElse(new SystemSettingEntity("auth.2fa.enabled", "false", "Enable SMS / Email Two-Factor Authentication"));
        setting.setSettingValue(String.valueOf(enabled));
        settingRepository.save(setting);
    }

    public String getAppName() {
        return settingRepository.findById("app.name")
                .map(SystemSettingEntity::getSettingValue)
                .orElse("CTH International Payment Gateway");
    }

    @Transactional
    public void setAppName(String name) {
        SystemSettingEntity setting = settingRepository.findById("app.name")
                .orElse(new SystemSettingEntity("app.name", "CTH International Payment Gateway", "Configurable Application Name"));
        setting.setSettingValue(name);
        settingRepository.save(setting);
    }

    private void sendAuthenticationNotification(UserEntity user, String message) {
        System.out.println("[2FA/SMS/Email Notification to " + user.getEmail() + " / " + user.getPhoneNumber() + "]: " + message);
    }
}
