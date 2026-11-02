package com.example.renogo.controller;

import com.example.renogo.dto.UserRequestDTO;
import com.example.renogo.dto.UserResponseDTO;
import com.example.renogo.service.UserService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserResponseDTO> getUsers() {
        return service.getAllUsers();
    }

    @PostMapping
    public UserResponseDTO createUser(
            @RequestBody UserRequestDTO request) {

        return service.createUser(request);
    }

    @PutMapping("/{id}")
    public UserResponseDTO updateUser(
            @PathVariable Long id,
            @RequestBody UserRequestDTO request) {

        return service.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(
            @PathVariable Long id) {

        service.deleteUser(id);
    }
}