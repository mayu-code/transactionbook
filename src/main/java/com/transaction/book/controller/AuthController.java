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

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
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
        SuccessResponse response = new SuccessResponse();
        User user = this.userServiceImpl.getUserByEmail(request.getEmail());
        if (user != null) {
            response.setMessage("User Already Present !");
            response.setHttpStatus(HttpStatus.ALREADY_REPORTED);
            response.setStatusCode(209);
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(response);
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
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
            response.setMessage("you are registerd successfully !");
            response.setHttpStatus(HttpStatus.OK);
            response.setStatusCode(200);
            return ResponseEntity.of(Optional.of(response));
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/send-otp")
    public ResponseEntity<SuccessResponse> sendOtp(@RequestParam() String email) {

        User user = this.userServiceImpl.getUserByEmail(email);

        SuccessResponse response = new SuccessResponse();

        if (user == null) {
            response.setMessage("Email is not associated with any account!");
            response.setHttpStatus(HttpStatus.BAD_REQUEST);
            response.setStatusCode(400);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (!user.isApproved()) {
            response.setMessage("you are not approve yet!");
            response.setHttpStatus(HttpStatus.BAD_REQUEST);
            response.setStatusCode(400);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        this.otpServiceImpl.generateAndSendOtp(email);
        response.setMessage("OTP sent successfully!");
        response.setHttpStatus(HttpStatus.OK);
        response.setStatusCode(200);

        return ResponseEntity.of(Optional.of(response));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<SuccessResponse> verifyOtp(@RequestParam(required = true) String email,
            @RequestParam(required = true) String otp) {

        SuccessResponse response = new SuccessResponse();

        try {
            if (this.otpServiceImpl.verifyOtp(email, otp)) {
                response.setMessage("otp varify successfully !");
                response.setHttpStatus(HttpStatus.OK);
                response.setStatusCode(200);
                return ResponseEntity.of(Optional.of(response));
            } else {
                response.setMessage("Invalid OTP!");
                response.setHttpStatus(HttpStatus.BAD_REQUEST);
                response.setStatusCode(400);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (Exception e) {
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

        SuccessResponse response = new SuccessResponse();

        try {

            if (this.otpServiceImpl.resetPassword(email, otp, password)) {
                response.setMessage("password reseted successfully !");
                response.setHttpStatus(HttpStatus.OK);
                response.setStatusCode(200);
                return ResponseEntity.of(Optional.of(response));
            } else {
                response.setMessage("session is expired ! or invalid otp");
                response.setHttpStatus(HttpStatus.BAD_REQUEST);
                response.setStatusCode(400);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage(e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest request) {
        SuccessResponse response = new SuccessResponse();
        User user = this.userServiceImpl.getUserByEmail(request.getEmail());
        if (user == null) {
            response.setMessage("Invalid UserName Or Password !");
            response.setHttpStatus(HttpStatus.UNAUTHORIZED);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        System.out.println(user.isApproved());
        if (!user.isApproved()) {
            response.setMessage("User Not Approved Yet ! please contact to App Owner !");
            response.setHttpStatus(HttpStatus.UNAUTHORIZED);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        UserDetails userDetails = this.customUserDetail.loadUserByUsername(request.getEmail());
        boolean isPasswordValid = new BCryptPasswordEncoder().matches(request.getPassword(), userDetails.getPassword());
        if (!isPasswordValid) {
            response.setMessage("Invalid Password !");
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        try {

            Authentication authentication = authenticate(user.getEmail(), request.getPassword());
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
        SuccessResponse response = new SuccessResponse();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setMessage("No token provided!");
            response.setHttpStatus(HttpStatus.UNAUTHORIZED);
            response.setStatusCode(401);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String jwt = authHeader.substring(7);

        try {
            JwtToken jwtToken = this.jwtTokenServiceImpl.getTokenByToken(jwt);
            if (jwtToken != null) {
                jwtToken.setActive(false);
                jwtToken.setLogoutAt(String.valueOf(Instant.now()));
                this.jwtTokenServiceImpl.addJwtToken(jwtToken);
            }

            response.setMessage("You are logged out successfully!");
            response.setHttpStatus(HttpStatus.OK);
            response.setStatusCode(200);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
