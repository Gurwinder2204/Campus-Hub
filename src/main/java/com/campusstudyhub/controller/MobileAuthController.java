package com.campusstudyhub.controller;

import com.campusstudyhub.dto.UserDto;
import com.campusstudyhub.dto.mobile.MobileAuthRequest;
import com.campusstudyhub.dto.mobile.MobileAuthResponse;
import com.campusstudyhub.dto.mobile.MobileRegisterRequest;
import com.campusstudyhub.dto.mobile.MobileUserDto;
import com.campusstudyhub.entity.User;
import com.campusstudyhub.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/mobile/auth")
public class MobileAuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public MobileAuthController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody MobileRegisterRequest request, HttpServletRequest httpRequest) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Passwords do not match"));
        }

        UserDto userDto = new UserDto();
        userDto.setFullName(request.getFullName());
        userDto.setEmail(request.getEmail());
        userDto.setPassword(request.getPassword());
        userDto.setConfirmPassword(request.getConfirmPassword());

        try {
            userService.register(userDto);
            Authentication authentication = authenticate(request.getEmail(), request.getPassword(), httpRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new MobileAuthResponse("Registration successful", toUserDto(userService.getByEmail(authentication.getName()))));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody MobileAuthRequest request, HttpServletRequest httpRequest) {
        try {
            Authentication authentication = authenticate(request.getEmail(), request.getPassword(), httpRequest);
            return ResponseEntity.ok(
                    new MobileAuthResponse("Login successful", toUserDto(userService.getByEmail(authentication.getName()))));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid email or password"));
        }
    }

    @GetMapping("/me")
    public MobileUserDto me(Authentication authentication) {
        return toUserDto(userService.getByEmail(authentication.getName()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    private Authentication authenticate(String email, String password, HttpServletRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(email, password));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        request.getSession(true).setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        return authentication;
    }

    private MobileUserDto toUserDto(User user) {
        return new MobileUserDto(user.getId(), user.getFullName(), user.getEmail(), user.getRole());
    }
}
