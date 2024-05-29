package org.froome.userservice.service;

import org.froome.userservice.exception.ConflictException;
import org.froome.userservice.exception.NotFoundException;
import org.froome.userservice.exception.UnauthorizedException;
import org.froome.userservice.model.User;
import org.froome.userservice.model.dto.UserDto;
import org.froome.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OrderService orderService;

    public UserDto register(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new ConflictException("Email already in use");
        }

        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new ConflictException("Username already in use");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEmail(userDto.getEmail());
        user.setAddress(userDto.getAddress());
        user.setAdmin(userDto.isAdmin());
        user = userRepository.save(user);
        return toDto(user);
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return jwtService.generateToken(user.toMap());
        }
        throw new UnauthorizedException("Invalid login credentials");
    }

    public List<UserDto> getUsers() {
        List<UserDto> users = new ArrayList<>();
        userRepository.findAll().forEach(user -> users.add(toDto(user)));
        return users;
    }

    public UserDto getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        return toDto(user);
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setAddress(userDto.getAddress());
        user.setAdmin(userDto.isAdmin());
        user = userRepository.save(user);
        return toDto(user);
    }

    public void deleteUser(Long id) {
        orderService.deleteOrdersByUserId(id);
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        userRepository.delete(user);
    }

    private UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setAddress(user.getAddress());
        dto.setAdmin(user.isAdmin());
        return dto;
    }

    public int countUsers() {
        return (int) userRepository.count();
    }
}

