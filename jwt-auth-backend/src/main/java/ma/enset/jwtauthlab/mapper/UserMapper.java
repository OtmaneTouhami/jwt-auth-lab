package ma.enset.jwtauthlab.mapper;

import ma.enset.jwtauthlab.dto.RegisterRequest;
import ma.enset.jwtauthlab.dto.UserResponse;
import ma.enset.jwtauthlab.entity.User;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class UserMapper {

    /**
     * Convert RegisterRequest DTO to User Entity
     * Note: Password should be encoded before calling this method
     */
    public User toEntity(RegisterRequest request, String encodedPassword) {
        if (request == null) {
            return null;
        }

        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(encodedPassword)
                .roles(request.getRoles() != null && !request.getRoles().isEmpty()
                        ? request.getRoles()
                        : new HashSet<>() {{ add("ROLE_USER"); }})
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();
    }

    /**
     * Convert User Entity to UserResponse DTO
     */
    public UserResponse toDto(User user) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
