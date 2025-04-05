package com.transaction.book.controller;

import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.transaction.book.constants.Role;
import com.transaction.book.dto.requestDTO.LoginRequest;
import com.transaction.book.dto.requestDTO.RegistrationRequest;
import com.transaction.book.dto.responseObjects.LoginResponse;
import com.transaction.book.dto.responseObjects.SuccessResponse;
import com.transaction.book.entities.JwtToken;
import com.transaction.book.entities.User;
import com.transaction.book.jwtSecurity.CustomUserDetail;
import com.transaction.book.jwtSecurity.JwtProvider;
import com.transaction.book.services.serviceImpl.JwtTokenServiceImpl;
import com.transaction.book.services.serviceImpl.OtpServiceImpl;
import com.transaction.book.services.serviceImpl.UserServiceImpl;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
@Slf4j
public class AuthController {

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private OtpServiceImpl otpServiceImpl;

    @Autowired
    private CustomUserDetail customUserDetail;

    @Autowired
    private JwtTokenServiceImpl jwtTokenServiceImpl;

    @PostMapping("/registerUser")
    public ResponseEntity<SuccessResponse> registerUser(@Valid @RequestBody RegistrationRequest request) {
        log.info("Registering user with email: {}", request.getEmail());
        SuccessResponse response = new SuccessResponse();
        User user = this.userServiceImpl.getUserByEmail(request.getEmail());
        if (user != null) {
            log.warn("User with email: {} already exists", request.getEmail());
            response.setMessage("User Already Present !");
            response.setHttpStatus(HttpStatus.ALREADY_REPORTED);
            response.setStatusCode(209);
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(response);
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            log.warn("Password and confirm password do not match for email: {}", request.getEmail());
            response.setMessage("password and confirm password does not match !");
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setMobileNo(request.getMobileNo());
        newUser.setName(request.getName());
        newUser.setPassword(new BCryptPasswordEncoder().encode(request.getPassword()));
        if (this.userServiceImpl.isEmptyUserTable()) {
            newUser.setRole(Role.ADMIN);
            newUser.setApproved(true);
        } else {
            newUser.setRole(Role.USER);
        }
        try {
            this.userServiceImpl.registerUser(newUser);
            log.info("User registered successfully with email: {}", request.getEmail());
            response.setMessage("you are registerd successfully !");
            response.setHttpStatus(HttpStatus.OK);
            response.setStatusCode(200);
            return ResponseEntity.of(Optional.of(response));
        } catch (Exception e) {
            log.error("Error registering user with email: {}: {}", request.getEmail(), e.getMessage());
            response.setMessage(e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/send-otp")
    public ResponseEntity<SuccessResponse> sendOtp(@RequestParam() String email) {
        log.info("Sending OTP to email: {}", email);
        User user = this.userServiceImpl.getUserByEmail(email);

        SuccessResponse response = new SuccessResponse();

        if (user == null) {
            log.warn("Email not associated with any account: {}", email);
            response.setMessage("Email is not associated with any account!");
            response.setHttpStatus(HttpStatus.BAD_REQUEST);
            response.setStatusCode(400);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (!user.isApproved()) {
            log.warn("User with email: {} is not approved", email);
            response.setMessage("you are not approve yet!");
            response.setHttpStatus(HttpStatus.BAD_REQUEST);
            response.setStatusCode(400);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        this.otpServiceImpl.generateAndSendOtp(email);
        log.info("OTP sent successfully to email: {}", email);
        response.setMessage("OTP sent successfully!");
        response.setHttpStatus(HttpStatus.OK);
        response.setStatusCode(200);

        return ResponseEntity.of(Optional.of(response));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<SuccessResponse> verifyOtp(@RequestParam(required = true) String email,
            @RequestParam(required = true) String otp) {
        log.info("Verifying OTP for email: {}", email);
        SuccessResponse response = new SuccessResponse();

        try {
            if (this.otpServiceImpl.verifyOtp(email, otp)) {
                log.info("OTP verified successfully for email: {}", email);
                response.setMessage("otp varify successfully !");
                response.setHttpStatus(HttpStatus.OK);
                response.setStatusCode(200);
                return ResponseEntity.of(Optional.of(response));
            } else {
                log.warn("Invalid OTP for email: {}", email);
                response.setMessage("Invalid OTP!");
                response.setHttpStatus(HttpStatus.BAD_REQUEST);
                response.setStatusCode(400);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (Exception e) {
            log.error("Error verifying OTP for email: {}: {}", email, e.getMessage());
            response.setMessage("Invalid OTP!");
            response.setHttpStatus(HttpStatus.BAD_REQUEST);
            response.setStatusCode(400);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<SuccessResponse> resetPassword(@RequestParam(required = true) String email,
            @RequestParam(required = true) String otp,
            @RequestParam(required = true) String password) {
        log.info("Resetting password for email: {}", email);
        SuccessResponse response = new SuccessResponse();

        try {

            if (this.otpServiceImpl.resetPassword(email, otp, password)) {
                log.info("Password reset successfully for email: {}", email);
                response.setMessage("password reseted successfully !");
                response.setHttpStatus(HttpStatus.OK);
                response.setStatusCode(200);
                return ResponseEntity.of(Optional.of(response));
            } else {
                log.warn("Session expired or invalid OTP for email: {}", email);
                response.setMessage("session is expired ! or invalid otp");
                response.setHttpStatus(HttpStatus.BAD_REQUEST);
                response.setStatusCode(400);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (Exception e) {
            log.error("Error resetting password for email: {}: {}", email, e.getMessage());
            e.printStackTrace();
            response.setMessage(e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest request) {
        log.info("Logging in user with email: {}", request.getEmail());
        SuccessResponse response = new SuccessResponse();
        User user = this.userServiceImpl.getUserByEmail(request.getEmail());
        if (user == null) {
            log.warn("Invalid username or password for email: {}", request.getEmail());
            response.setMessage("Invalid UserName Or Password !");
            response.setHttpStatus(HttpStatus.UNAUTHORIZED);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        System.out.println(user.isApproved());
        if (!user.isApproved()) {
            log.warn("User with email: {} is not approved", request.getEmail());
            response.setMessage("User Not Approved Yet ! please contact to App Owner !");
            response.setHttpStatus(HttpStatus.UNAUTHORIZED);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        UserDetails userDetails = this.customUserDetail.loadUserByUsername(request.getEmail());
        boolean isPasswordValid = new BCryptPasswordEncoder().matches(request.getPassword(), userDetails.getPassword());
        if (!isPasswordValid) {
            log.warn("Invalid password for email: {}", request.getEmail());
            response.setMessage("Invalid Password !");
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        try {

            Authentication authentication = authenticate(user.getEmail(), request.getPassword());
            log.info("User logged in successfully with email: {}", request.getEmail());
            String role = user.getRole().toString();
            JwtToken token = JwtProvider.generateJwt(authentication, request.getClientType());

            JwtToken jwtToken = new JwtToken();
            jwtToken.setClientType(request.getClientType());
            jwtToken.setIssuedAt(String.valueOf(Instant.now()));
            jwtToken.setUser(user);
            jwtToken.setExpiration(token.getExpiration());
            jwtToken.setToken(token.getToken());
            jwtTokenServiceImpl.addJwtToken(jwtToken);

            LoginResponse response2 = new LoginResponse();
            response2.setRole(role);
            response2.setToken(token.getToken());
            response2.setMessage("Login User  Successfully !");
            response2.setHttpStatus(HttpStatus.OK);
            response2.setStatusCode(200);
            response2.setExpiration(token.getExpiration());
            return ResponseEntity.of(Optional.of(response2));
        } catch (Exception e) {
            log.error("Error logging in user with email: {}: {}", request.getEmail(), e.getMessage());
            response.setMessage(e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private Authentication authenticate(String email, String password) {
        UserDetails userDetails = this.customUserDetail.loadUserByUsername(email);
        if (userDetails == null) {
            throw new UsernameNotFoundException("bad credentials");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }

    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse> logoutUser(
            @RequestHeader(name = "Authorization", required = false) String authHeader) {
        log.info("Logging out user");
        SuccessResponse response = new SuccessResponse();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("No token provided for logout");
            response.setMessage("No token provided!");
            response.setHttpStatus(HttpStatus.UNAUTHORIZED);
            response.setStatusCode(401);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String jwt = authHeader.substring(7);

        try {
            JwtToken jwtToken = this.jwtTokenServiceImpl.getTokenByToken(jwt);
            if (jwtToken != null) {
                log.info("Token invalidated successfully");
                jwtToken.setActive(false);
                jwtToken.setLogoutAt(String.valueOf(Instant.now()));
                this.jwtTokenServiceImpl.addJwtToken(jwtToken);
            }

            response.setMessage("You are logged out successfully!");
            response.setHttpStatus(HttpStatus.OK);
            response.setStatusCode(200);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error logging out user: {}", e.getMessage());
            response.setMessage(e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
