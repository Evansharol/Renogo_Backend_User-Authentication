package com.example.renogo.service;

import com.example.renogo.dto.UserRequestDTO;
import com.example.renogo.dto.UserResponseDTO;
import com.example.renogo.entity.User;
import com.example.renogo.repository.UserRepository;
import com.example.renogo.security.PasswordService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceTests {

    @Test
    void createUserGeneratesUserIdWhenOmitted() {
        UserRepository repository = mock(UserRepository.class);
        when(repository.saveAndFlush(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            assertTrue(user.getUserId().startsWith("TMP"));
            assertTrue(user.getUserId().length() <= 32);
            user.setId(7L);
            return user;
        });
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserRequestDTO request = new UserRequestDTO();
        request.setName("New Dealer");
        request.setEmail("new.dealer@example.com");
        request.setRole("dealer");
        request.setPassword("password123");

        UserResponseDTO created = new UserService(repository, new PasswordService()).createUser(request);

        assertEquals("D007", created.getUserId());
    }
}
