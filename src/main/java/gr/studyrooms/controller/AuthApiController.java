package gr.studyrooms.controller;

import gr.studyrooms.dto.AuthRequest;
import gr.studyrooms.dto.AuthResponse;
import gr.studyrooms.dto.RegisterRequest;
import gr.studyrooms.security.JwtService;
import gr.studyrooms.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthApiController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthApiController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserService userService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/auth/token")
    public ResponseEntity<?> token(@Valid @RequestBody AuthRequest request) {
        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );

            return ResponseEntity.ok(
                    new AuthResponse(
                            "Bearer",
                            jwtService.generateToken(authentication)
                    )
            );

        } catch (BadCredentialsException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }
    }

    @PostMapping("/auth/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        userService.registerStudent(request);
        return ResponseEntity.ok("registered");
    }
}