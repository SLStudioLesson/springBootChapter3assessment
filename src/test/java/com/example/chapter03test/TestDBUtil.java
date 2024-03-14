package com.example.chapter03test;

import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

public class TestDBUtil {
    public static void resetDB(TestEntityManager em) throws Exception {
        em.getEntityManager().createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        em.getEntityManager().createNativeQuery("TRUNCATE TABLE posts RESTART IDENTITY").executeUpdate();
        em.getEntityManager().createNativeQuery("TRUNCATE TABLE users RESTART IDENTITY").executeUpdate();
        em.getEntityManager().createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }
}
