package ma.enset.jwtauthlab.service;

import ma.enset.jwtauthlab.dto.RegisterRequest;
import ma.enset.jwtauthlab.dto.UserResponse;
import ma.enset.jwtauthlab.entity.User;
import ma.enset.jwtauthlab.exception.UserAlreadyExistsException;
import ma.enset.jwtauthlab.mapper.UserMapper;
import ma.enset.jwtauthlab.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, 
                      PasswordEncoder passwordEncoder,
                      UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    /**
     * Register a new user
     */
    public UserResponse registerUser(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username is already taken: " + request.getUsername());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email is already in use: " + request.getEmail());
        }

        // Encode password
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // Map DTO to Entity
        User user = userMapper.toEntity(request, encodedPassword);

        // Save user
        User savedUser = userRepository.save(user);

        // Return response DTO
        return userMapper.toDto(savedUser);
    }

    /**
     * Find user by username
     */
    @Transactional(readOnly = true)
    public UserResponse findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return userMapper.toDto(user);
    }

    /**
     * Find user by email
     */
    @Transactional(readOnly = true)
    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return userMapper.toDto(user);
    }

    /**
     * Get all users
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Check if username exists
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Check if email exists
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
