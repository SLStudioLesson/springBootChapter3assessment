package com.example.chapter03test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.File;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;

import com.example.chapter03test.controller.GeneralController;
import com.example.chapter03test.exception.GlobalExceptionHandler;
import com.example.chapter03test.model.Post;

import jakarta.transaction.Transactional;

@ExtendWith({
    OutputCaptureExtension.class
})
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
public class Q3Test {
    private MockMvc mockMvc;

    @Autowired
    private GeneralController target;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setup() throws Exception {
        mockMvc = MockMvcBuilders
            .standaloneSetup(target)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

        TestDBUtil.resetDB(em);
        TestDBUtil.insertPosts(em, 1, 5);
    }

    @AfterEach
    void setDown() throws Exception {
        TestDBUtil.resetDB(em);
    }

    @Tag("Q3")
    @Test
    @WithMockUser(username = "general", roles = {"GENERAL"})
    void addPostTest() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "test.png", "image/png", "testdata".getBytes());
        mockMvc.perform(multipart("/general/posts")
            .file(image)
            .param("content", "testcontent")
            .with(csrf()))
            .andExpect(status().is(302))
            .andExpect(redirectedUrl("/general/posts"));

        int id = em.getEntityManager().createQuery("from Post p").getResultList().size();
        File file = new File("uploads/images/" + id + ".png");

        Post expected = em.find(Post.class, id);
        assertEquals(6L, expected.getId());
        assertEquals("testcontent", expected.getContent());

        assertTrue(file.exists());

        // テスト後にアップロードされたファイルを削除する
        if (file.exists()) {
            file.delete();
        }
    }

    @Tag("Q3")
    @Test
    @WithMockUser(username = "general", roles = {"GENERAL"})
    void addPostTest_invalidContentGreaterThan140() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", new byte[0]);
        MvcResult result = mockMvc.perform(multipart("/general/posts")
            .file(image)
            .param("content", "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901")
            .with(csrf()))
            .andExpect(status().is(200))
            .andExpect(view().name("postsList"))
            .andReturn();
        BindingResult bindingResult = (BindingResult) result.getModelAndView().getModel()
            .get(BindingResult.MODEL_KEY_PREFIX + "postForm");
        String message = bindingResult.getFieldErrors().toString();
        assertThat(message).contains("Content must be less than or equal to 140 characters");
    }

    @Tag("Q3")
    @Test
    @WithMockUser(username = "general", roles = {"GENERAL"})
    void addPostTest_invalidFileExtension(CapturedOutput output) throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "testdata".getBytes());
        mockMvc.perform(multipart("/general/posts")
            .file(image)
            .param("content", "testcontent")
            .with(csrf()))
            .andExpect(status().is(200))
            .andExpect(view().name("error/custom-error"))
            .andExpect(model().attribute("errorMessage", "Only .png files are allowed."));

        assertThat(output).contains("Image loading error occurred: Only .png files are allowed.");
    }

    @Tag("Q3")
    @Test
    @WithMockUser(username = "general", roles = {"GENERAL"})
    void addPostTest_invalidSize(CapturedOutput output) throws Exception {
        Post post = new Post();
        post.setId(1L);
        MockMultipartFile image = new MockMultipartFile("image", "test.png", "image/png", new byte[1024 * 1025 * 2]);
        mockMvc.perform(multipart("/general/posts")
            .file(image)
            .param("content", "testcontent")
            .with(csrf()))
            .andExpect(status().is(200))
            .andExpect(view().name("error/custom-error"))
            .andExpect(model().attribute("errorMessage", "File size must be less than 2MB."));

        assertThat(output).contains("Image loading error occurred: File size must be less than 2MB.");
    }
}
