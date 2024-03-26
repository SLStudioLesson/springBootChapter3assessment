package com.example.chapter03test;

import java.time.LocalDateTime;

import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.chapter03test.model.Post;
import com.example.chapter03test.model.User;

public class TestDBUtil {
    public static void resetDB(TestEntityManager em) throws Exception {
        em.getEntityManager().createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        em.getEntityManager().createNativeQuery("TRUNCATE TABLE posts RESTART IDENTITY").executeUpdate();
        em.getEntityManager().createNativeQuery("TRUNCATE TABLE users RESTART IDENTITY").executeUpdate();
        em.getEntityManager().createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }

    public static void insertPosts(TestEntityManager em, int start, int end) throws Exception {
        for (int i = start; i <= end; i++) {
            User user = new User("test@example.com", "password", "user" + i, i, "ROLE_GENERAL");
            LocalDateTime dateTime = LocalDateTime.parse("2024-03-01T00:00:00").plusDays(i);
            Post post = new Post("content" + i, "imageUrl", user);
            post.setCreatedAt(dateTime);

            em.persist(user);
            em.persist(post);
        }
    }
}
