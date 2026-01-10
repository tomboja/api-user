package com.olmaitsolutions.edu.boja.apiusers.apiusers.service;

import com.olmaitsolutions.edu.boja.apiusers.apiusers.dto.CreateUserDTO;
import com.olmaitsolutions.edu.boja.apiusers.apiusers.dto.UserDTO;
import com.olmaitsolutions.edu.boja.apiusers.apiusers.exception.UserNotFoundException;
import com.olmaitsolutions.edu.boja.apiusers.apiusers.mapper.UserMapper;
import com.olmaitsolutions.edu.boja.apiusers.apiusers.model.User;
import com.olmaitsolutions.edu.boja.apiusers.apiusers.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper mapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDTO> listUsers() {
        return userRepository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public UserDTO getUser(Long id) {
        return userRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new UserNotFoundException("User with id %d not found".formatted(id)));
    }

    public UserDTO createUser(CreateUserDTO dto) {
        User user = mapper.toEntity(dto);
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setCreatedDate(OffsetDateTime.now());
        user.setEmailVerified(false);
        if (user.getActiveStatus() == null) user.setActiveStatus(false);
        if (user.getUserRole() == null) user.setUserRole("USER");
        User saved = userRepository.save(user);
        return mapper.toDto(saved);
    }

    public void deleteUserById(Long id) {
        if (id == null) {
            throw new UserNotFoundException("User id must not be null");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id %d not found".formatted(id)));
        userRepository.delete(user);
    }

    public void deleteUserByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new UserNotFoundException("Username must not be blank");
        }
        User user = userRepository.findByUsername(username.trim())
                .orElseThrow(() -> new UserNotFoundException("User with username %s not found".formatted(username.trim())));
        userRepository.delete(user);
    }
}
