package com.example.chapter03test.controller;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.chapter03test.exception.GlobalExceptionHandler;
import com.example.chapter03test.model.Post;
import com.example.chapter03test.service.PostService;

@ExtendWith({
    MockitoExtension.class,
    OutputCaptureExtension.class
})
@SpringBootTest
public class GeneralControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private GeneralController target;

    @Mock
    private PostService mockPostService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(target)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Tag("Q3")
    @Test
    @WithMockUser(username = "general", roles = {"GENERAL"})
    void addPostTest_noImage() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", new byte[0]);
        mockMvc.perform(multipart("/general/posts")
            .file(image)
            .param("content", "testcontent")
            .with(csrf()))
            .andExpect(status().is(302))
            .andExpect(redirectedUrl("/general/posts"));
    }

    @Tag("Q3")
    @Test
    @WithMockUser(username = "general", roles = {"GENERAL"})
    void addPostTest_normalValue() throws Exception {
        Post post = new Post();
        post.setId(1L);
        doReturn(post).when(mockPostService).createPost("testcontent", null, null);
        MockMultipartFile image = new MockMultipartFile("image", "test.png", "image/png", "testdata".getBytes());
        File file = new File("uploads/images/" + post.getId() + ".png");

        mockMvc.perform(multipart("/general/posts")
            .file(image)
            .param("content", "testcontent")
            .with(csrf()))
            .andExpect(status().is(302))
            .andExpect(redirectedUrl("/general/posts"));

        assertTrue(file.exists());

        // テスト後にアップロードされたファイルを削除する
        if (file.exists()) {
            file.delete();
        }
    }

    @Tag("Q3")
    @Test
    @WithMockUser(username = "general", roles = {"GENERAL"})
    void addPostTest_invalidExtension(CapturedOutput output) throws Exception {
        Post post = new Post();
        post.setId(1L);
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
