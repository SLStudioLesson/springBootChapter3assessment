package com.example.chapter03test;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;

import com.example.chapter03test.controller.AdminController;
import com.example.chapter03test.model.User;

@SpringBootTest
public class Q2Test {
    private MockMvc mockMvc;

    @Autowired
    private AdminController target;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(target)
            .build();
    }

    @Tag("Q2")
    @Test
    void addUserTest_invalidAll() throws Exception {
        User user = new User("aaa", "", "", null, null);
        MvcResult result = mockMvc.perform(post("/admin/user/add")
            .flashAttr("user", user))
            .andExpect(model().hasErrors())
            .andExpect(view().name("admin/addUser"))
            .andReturn();
        BindingResult bindingResult = (BindingResult) result.getModelAndView().getModel()
            .get(BindingResult.MODEL_KEY_PREFIX + "user");
        String message = bindingResult.getFieldErrors().toString();
        assertThat(message).contains("Invalid email format.");
        assertThat(message).contains("Password must be at least 8 characters.");
        assertThat(message).contains("Password must contain only alphanumeric characters and specific symbols.");
        assertThat(message).contains("Name is required.");
        assertThat(message).contains("Age is required.");
    }

    @Tag("Q2")
    @Test
    void addUserTest_invalidAgeLessThan0() throws Exception {
        User user = new User("test@example.com", "password", "user", -1, null);
        MvcResult result = mockMvc.perform(post("/admin/user/add")
            .flashAttr("user", user))
            .andExpect(model().hasErrors())
            .andExpect(view().name("admin/addUser"))
            .andReturn();
        BindingResult bindingResult = (BindingResult) result.getModelAndView().getModel()
            .get(BindingResult.MODEL_KEY_PREFIX + "user");
        String message = bindingResult.getFieldErrors().toString();
        assertThat(message).contains("Age must be greater than or equal to 0.");
    }

    @Tag("Q2")
    @Test
    void addUserTest_invalidAgeGreaterThan0() throws Exception {
        User user = new User("test@example.com", "password", "user", 101, null);
        MvcResult result = mockMvc.perform(post("/admin/user/add")
            .flashAttr("user", user))
            .andExpect(model().hasErrors())
            .andExpect(view().name("admin/addUser"))
            .andReturn();
        BindingResult bindingResult = (BindingResult) result.getModelAndView().getModel()
            .get(BindingResult.MODEL_KEY_PREFIX + "user");
        String message = bindingResult.getFieldErrors().toString();
        assertThat(message).contains("Age must be less than or equal to 100.");
    }
}
