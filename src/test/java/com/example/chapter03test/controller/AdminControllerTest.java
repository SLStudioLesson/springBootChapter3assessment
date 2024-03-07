package com.example.chapter03test.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;

import com.example.chapter03test.model.User;
import com.example.chapter03test.service.UserService;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private AdminController target;

    @Mock
    private UserService mockUserService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(target)
            .build();
    }

    @Tag("Q2")
    @Test
    void addUserTest_normalValue() throws Exception {
        User user = new User("test@example.com", "password", "testuser", 50, null);
        mockMvc.perform(post("/admin/user/add")
            .flashAttr("user", user))
            .andExpect(model().hasNoErrors());
    }

    @Tag("Q2")
    @ParameterizedTest
    @CsvSource({
        "test, password, testuser, 50, Invalid email format.",
        "test@example.com, pass, testuser, 50, Password must be at least 8 characters.",
        "test@example.com, ああああああああああ, testuser, 50, Password must contain only alphanumeric characters and specific symbols.",
        "test@example.com, password, '', 50, Name is required.",
        "test@example.com, password, testuser, , Age is required.",
        "test@example.com, password, testuser, -1, Age must be greater than or equal to 0.",
        "test@example.com, password, testuser, 101, Age must be less than or equal to 100.",
    })
    void addUserTest_abnormalValue(String email, String password, String name, Integer age, String expected) throws Exception {
        User user = new User(email, password, name, age, null);
        MvcResult result = mockMvc.perform(post("/admin/user/add")
            .flashAttr("user", user))
            .andExpect(model().hasErrors())
            .andExpect(view().name("admin/addUser"))
            .andReturn();
        BindingResult bindingResult = (BindingResult) result.getModelAndView().getModel()
            .get(BindingResult.MODEL_KEY_PREFIX + "user");
        String message = bindingResult.getFieldError().getDefaultMessage();
        assertEquals(expected, message);
    }
}
