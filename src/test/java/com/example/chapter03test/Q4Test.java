package com.example.chapter03test;


import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import com.example.chapter03test.config.PostCountBatch;

@DataJpaTest
@Import(PostCountBatch.class)
public class Q4Test {
    @Autowired
    private PostCountBatch postCountBatch;

    @Autowired
    private TestEntityManager em;

    private final PrintStream systemOut = System.out;
    private ByteArrayOutputStream testOut;

    @BeforeEach
    void setUp() throws Exception {
        TestDBUtil.resetDB(em);
        TestDBUtil.insertPosts(em, 1, 2);
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    @AfterEach
    void setDown() throws Exception {
        System.setOut(systemOut);

        TestDBUtil.resetDB(em);
    }

    @Tag("Q4")
    @Test
    void reportCurrentPostCountTest() throws Exception {
        postCountBatch.reportCurrentPostCount();
        assertThat(testOut.toString().trim()).contains("User ID: 1, Name: user1, Posts Count: 1");
        assertThat(testOut.toString().trim()).contains("User ID: 2, Name: user2, Posts Count: 1");
    }
}
