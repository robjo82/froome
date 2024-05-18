package org.froome.orderservice.service;

import lombok.RequiredArgsConstructor;
import org.froome.orderservice.exception.NotFoundException;
import org.froome.orderservice.model.User;
import org.froome.orderservice.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found."));
    }
}
