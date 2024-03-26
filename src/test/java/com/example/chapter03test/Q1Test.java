package com.example.chapter03test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
public class Q1Test {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .build();
    }

    @Tag("Q1")
    @Test
    @WithMockUser(username = "general", roles = {"GENERAL"})
    void accessWithGeneralToGeneral() throws Exception {
        mockMvc.perform(get("/general/posts"))
            .andExpect(status().isOk());
    }

    @Tag("Q1")
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void accessWithAdminToGeneral() throws Exception {
        mockMvc.perform(get("/general/posts"))
            .andExpect(status().isOk());
    }

    @Tag("Q1")
    @Test
    @WithMockUser(username = "general", roles = {"GENERAL"})
    void accessWithGeneralToAdmin() throws Exception {
        mockMvc.perform(get("/admin/users"))
            .andExpect(status().isForbidden());
    }

    @Tag("Q1")
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void accessWithAdminToAdmin() throws Exception {
        mockMvc.perform(get("/admin/users"))
            .andExpect(status().isOk());
    }
}
