package com.example.renogo.service;

import com.example.renogo.dto.UserRequestDTO;
import com.example.renogo.dto.UserResponseDTO;
import com.example.renogo.entity.User;
import com.example.renogo.mapper.UserMapper;
import com.example.renogo.repository.UserRepository;
import com.example.renogo.security.PasswordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordService passwordService;

    public UserService(UserRepository repository, PasswordService passwordService) {
        this.repository = repository;
        this.passwordService = passwordService;
    }

    public List<UserResponseDTO> getAllUsers() {
        return repository.findAll()
                .stream()
                .map(UserMapper::toDTO)
                .toList();
    }

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO request) {

        User user = UserMapper.toEntity(request);
        user.setPassword(passwordService.hash(request.getPassword()));

        String requestedUserId = normalizeUserId(user.getUserId());
        user.setUserId(requestedUserId == null
                ? "TMP" + UUID.randomUUID().toString().replace("-", "").substring(0, 29)
                : requestedUserId);
        User savedUser = repository.saveAndFlush(user);
        if (requestedUserId == null) {
            savedUser.setUserId(generateUserId(savedUser));
        }

        return UserMapper.toDTO(repository.save(savedUser));
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, UserRequestDTO request) {

        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordService.hash(request.getPassword()));
        }
        user.setStatus(request.getStatus());

        return UserMapper.toDTO(repository.save(user));
    }

    public void deleteUser(Long id) {
        repository.deleteById(id);
    }

    private String normalizeUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            return null;
        }
        return userId.trim().toUpperCase(Locale.ROOT);
    }

    private String generateUserId(User user) {
        String role = user.getRole() == null ? "" : user.getRole().toLowerCase(Locale.ROOT);
        String prefix = switch (role.replaceAll("[^a-z]", "")) {
            case "admin", "administrator" -> "A";
            case "dealer" -> "D";
            case "manufacturer" -> "M";
            case "financehead" -> "F";
            case "salesmanager" -> "SM";
            case "dealermanager" -> "DM";
            default -> "U";
        };
        return prefix + String.format(Locale.ROOT, "%03d", user.getId());
    }
}