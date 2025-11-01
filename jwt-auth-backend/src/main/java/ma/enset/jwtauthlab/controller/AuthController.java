package ma.enset.jwtauthlab.controller;

import jakarta.validation.Valid;
import ma.enset.jwtauthlab.dto.LoginRequest;
import ma.enset.jwtauthlab.dto.LoginResponse;
import ma.enset.jwtauthlab.dto.RegisterRequest;
import ma.enset.jwtauthlab.dto.UserResponse;
import ma.enset.jwtauthlab.service.JwtService;
import ma.enset.jwtauthlab.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, 
                         JwtService jwtService, 
                         UserDetailsService userDetailsService,
                         UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse userResponse = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // Authenticate user
        var auth = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        authenticationManager.authenticate(auth);
        
        // Load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        
        // Get user info for response
        UserResponse user = userService.findByUsername(request.getUsername());
        
        // Generate JWT token
        String token = jwtService.generateToken(userDetails.getUsername(), 
                Map.of("roles", userDetails.getAuthorities()));
        
        // Build response
        LoginResponse response = new LoginResponse(token, user.getUsername(), user.getEmail());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("message", "Bonjour, endpoint protégé OK ✅");
    }
}
