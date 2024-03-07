package com.example.chapter03test.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.example.chapter03test.model.Post;
import com.example.chapter03test.model.User;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
public class HomeRestControllerTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        User user1 = new User("test1@example.com", "password", "user1", 21, "ROLE_GENERAL");
        User user2 = new User("test2@example.com", "password", "user2", 21, "ROLE_GENERAL");
        Post post1 = new Post("content1", "url", user1);
        Post post2 = new Post("content2", "url", user1);
        Post post3 = new Post("content3", "url", user2);
        post1.setCreatedAt(LocalDateTime.parse("2024-03-07T00:00:00"));
        post2.setCreatedAt(LocalDateTime.parse("2024-03-09T00:00:00"));
        post3.setCreatedAt(LocalDateTime.parse("2024-03-08T00:00:00"));
        post1.setUser(user1);
        post2.setUser(user1);
        post3.setUser(user2);

        em.persist(user1);
        em.persist(user2);
        em.persist(post1);
        em.persist(post2);
        em.persist(post3);
    }

    @AfterEach
    void setDown() throws Exception {
        em.getEntityManager().createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        em.getEntityManager().createNativeQuery("TRUNCATE TABLE posts RESTART IDENTITY").executeUpdate();
        em.getEntityManager().createNativeQuery("TRUNCATE TABLE users RESTART IDENTITY").executeUpdate();
        em.getEntityManager().createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }

    @Tag("Q5")
    @Test
    void getNewestPostsTest() throws Exception {
        mockMvc.perform(get("/api"))
            .andExpect(status().isOk())
            .andExpect(content().json("{\"id\":2,\"content\":\"content2\",\"imageUrl\":\"url\",\"createdAt\":\"2024-03-09T00:00:00\"}"));
    }
}
