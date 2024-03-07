package com.example.chapter03test;

import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import com.example.chapter03test.config.PostCountBatch;
import com.example.chapter03test.model.Post;
import com.example.chapter03test.model.User;

@DataJpaTest
@Import(PostCountBatch.class)
public class PostCountBatchTest {

    @Autowired
    private PostCountBatch postCountBatch;

    @Autowired
    private TestEntityManager em;

    private final PrintStream systemOut = System.out;
    private ByteArrayOutputStream testOut;

    private static final List<User> userDataList;
    static {
        User user1 = new User("test1@example.com", "password", "user1", 21, "ROLE_GENERAL");
        User user2 = new User("test2@example.com", "password", "user2", 21, "ROLE_GENERAL");
        Post post1 = new Post("content1", "url", user1);
        Post post2 = new Post("content2", "url", user1);
        Post post3 = new Post("content3", "url", user2);

        user1.setPosts(new HashSet<Post>(Arrays.asList(
            post1,
            post2
        )));

        user2.setPosts(new HashSet<Post>(Arrays.asList(
            post3
        )));

        userDataList = Arrays.asList(user1, user2);
    }

    @BeforeEach
    void setUp() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        userDataList.forEach(user -> {
            em.persist(user);
            user.getPosts().forEach(post -> {
                em.persist(post);
            });
        });
    }

    @AfterEach
    void setDown() throws Exception {
        System.setOut(systemOut);

        em.getEntityManager().createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        em.getEntityManager().createNativeQuery("TRUNCATE TABLE posts RESTART IDENTITY").executeUpdate();
        em.getEntityManager().createNativeQuery("TRUNCATE TABLE users RESTART IDENTITY").executeUpdate();
        em.getEntityManager().createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }

    @Tag("Q4")
    @Test
    void reportCurrentPostCountTest() throws Exception {
        postCountBatch.reportCurrentPostCount();
        userDataList.forEach(user -> {
            assertThat(testOut.toString().trim()).contains("User ID: " + user.getId() + ", Name: " + user.getName() + ", Posts Count: " + user.getPosts().size());
        });
    }
}
