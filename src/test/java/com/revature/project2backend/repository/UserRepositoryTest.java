package com.revature.project2backend.repository;

import com.revature.project2backend.model.UserEntity;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setUsername("bogus");
        userEntity.setEmail("bogus@email.com");
        userEntity.setPassword("hashed_password");
    }

    @Test @Transactional
    void findByEmail_existingEmail() {
        entityManager.persist(userEntity);
        entityManager.flush();

        Optional<UserEntity> found = userRepository.findByEmail(userEntity.getEmail());

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(userEntity.getEmail());
    }

    @Test
    void findByEmail_nonExistingEmail() {
        Optional<UserEntity> found = userRepository.findByEmail("nonexistent@email.com");

        assertThat(found).isNotPresent();
    }

    @Test @Transactional
    void existsByEmail_existingEmail() {
        entityManager.persist(userEntity);
        entityManager.flush();

        boolean exists = userRepository.existsByEmail(userEntity.getEmail());

        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_nonExistingEmail() {
        boolean exists = userRepository.existsByEmail("nonexistent@email.com");

        assertThat(exists).isFalse();
    }

    @Test
    void findByEmail_nullEmail() {
        Optional<UserEntity> found = userRepository.findByEmail(null);

        assertThat(found).isNotPresent();
    }

    @Test
    void existsByEmail_nullEmail() {
        boolean exists = userRepository.existsByEmail(null);

        assertThat(exists).isFalse();
    }

    @Test @Transactional
    void findByEmail_multipleUsersWithSameEmail() {
        UserEntity anotherUser = new UserEntity();
        anotherUser.setUsername("anotherUser");
        anotherUser.setEmail(userEntity.getEmail());
        anotherUser.setPassword("another_hashed_password");

        entityManager.persist(userEntity);
        entityManager.flush();

        PersistenceException thrown = assertThrows(PersistenceException.class, () -> {
            entityManager.persist(anotherUser);
            entityManager.flush();
        });

        assertTrue(thrown.getCause() instanceof ConstraintViolationException, "Expected ConstraintViolationException as the cause of the PersistenceException");
    }

    @Test @Transactional
    void findByEmail_existingEmail_caseInsensitive() {
        entityManager.persist(userEntity);
        entityManager.flush();

        String mixedCaseEmail = "BogUs@eMAil.com";
        Optional<UserEntity> found = userRepository.findByEmail(mixedCaseEmail);

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(userEntity.getEmail());
    }

    @Test @Transactional
    void existsByEmail_existingEmail_caseInsensitive() {
        entityManager.persist(userEntity);
        entityManager.flush();

        String mixedCaseEmail = "BogUs@eMAil.com";
        boolean exists = userRepository.existsByEmail(mixedCaseEmail);

        assertThat(exists).isTrue();
    }

    @Test
    void findByEmail_emptyEmail() {
        Optional<UserEntity> found = userRepository.findByEmail("");

        assertThat(found).isNotPresent();
    }

    @Test
    void existsByEmail_emptyEmail() {
        boolean exists = userRepository.existsByEmail("");

        assertThat(exists).isFalse();
    }

    @Test @Transactional
    void findByUsername_existingUsername() {
        entityManager.persist(userEntity);
        entityManager.flush();

        Optional<UserEntity> found = userRepository.findByUsername(userEntity.getUsername());

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo(userEntity.getUsername());
    }

    @Test
    void findByUsername_nonExistingUsername() {
        Optional<UserEntity> found = userRepository.findByUsername("nonexistentUsername");

        assertThat(found).isNotPresent();
    }

    @Test
    void findByUsername_nullUsername() {
        Optional<UserEntity> found = userRepository.findByUsername(null);

        assertThat(found).isNotPresent();
    }

    @Test @Transactional
    void save_newUser() {
        UserEntity savedUser = userRepository.save(userEntity);

        Optional<UserEntity> found = userRepository.findById(savedUser.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo(userEntity.getUsername());
        assertThat(found.get().getEmail()).isEqualTo(userEntity.getEmail());
        assertThat(found.get().getPassword()).isEqualTo(userEntity.getPassword());
    }

    @Test @Transactional
    void deleteById_existingUserId() {
        entityManager.persist(userEntity);
        entityManager.flush();

        userRepository.deleteById(userEntity.getId());
        entityManager.flush();

        Optional<UserEntity> found = userRepository.findById(userEntity.getId());
        assertThat(found).isNotPresent();
    }

    @Test @Transactional
    void delete_existingUser() {
        entityManager.persist(userEntity);
        entityManager.flush();

        userRepository.delete(userEntity);
        entityManager.flush();

        Optional<UserEntity> found = userRepository.findById(userEntity.getId());
        assertThat(found).isNotPresent();
    }



}
