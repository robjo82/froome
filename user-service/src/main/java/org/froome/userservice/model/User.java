package org.froome.userservice.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Map;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin;

    public Map<String, Object> toMap() {
        return Map.of(
                "id", id,
                "username", username,
                "email", email,
                "address", address,
                "isAdmin", isAdmin
        );
    }
}
