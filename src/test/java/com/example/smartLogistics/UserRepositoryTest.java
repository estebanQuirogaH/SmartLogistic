package com.example.smartLogistics;

import com.projectStore.entity.User;
import com.projectStore.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByEmail() {
        String email = "admin2@gmail.com";
        Optional<User> user = userRepository.findByEmail(email);

        assertTrue(user.isPresent(), "El usuario no fue encontrado en la base de datos");
        System.out.println("Usuario encontrado: " + user.get().getEmail());
    }
}
