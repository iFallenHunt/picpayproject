package com.picpayproject.repositories;

import com.picpayproject.domain.user.User;
import com.picpayproject.domain.user.UserType;
import com.picpayproject.dtos.UserDTO;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager entityManager;

    /*
    Essa classe de teste, testa a inserção de novos usuarios no banco
    * */
    @Test
    @DisplayName("Should get User sucessfuly from DB")
    void testFindUserByDocumentCase1() {
        String document = "99999999901";
        UserDTO data = new UserDTO("Lucas", "Teste", document, new BigDecimal(10), "test@gmail.com", "1234", UserType.COMMON);
        this.createUser(data);

        Optional<User> result = this.userRepository.findUserByDocument(document);

        assertThat(result.isPresent()).isTrue();
    }

    /*
    Essa classe de teste, verifica se o usuario não existe
    * */
    @Test
    @DisplayName("Should not get User from DB when user not exists")
    void testFindUserByDocumentCase2() {
        String document = "99999999901";

        Optional<User> result = this.userRepository.findUserByDocument(document);

        assertThat(result.isEmpty()).isTrue();
    }

    private User createUser(UserDTO data) {
        User newUser = new User(data);
        this.entityManager.persist(newUser);
        return newUser;
    }
}
