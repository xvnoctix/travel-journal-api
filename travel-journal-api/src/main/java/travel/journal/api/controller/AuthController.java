package travel.journal.api.controller;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import travel.journal.api.entities.User;
import travel.journal.api.payload.request.LoginRequest;
import travel.journal.api.payload.response.JwtResponse;
import travel.journal.api.security.jwt.JwtUtils;
import travel.journal.api.security.services.UserDetailsImpl;
import travel.journal.api.service.UserService;

import java.util.Optional;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtUtils jwtUtils;
    private final UserService userService;


    public AuthController(UserService userService) {
        this.userService = userService;

    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Optional<User> existUser=userService.findUserByEmail(loginRequest.getEmail());
        if(existUser.isPresent()){
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            return ResponseEntity.ok(new JwtResponse(jwt));

        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("Acesta este un test securizat");
    }

}
