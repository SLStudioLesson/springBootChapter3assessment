package com.example.chapter03test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.chapter03test.form.PostForm;
import com.example.chapter03test.service.PostService;

@Controller
@RequestMapping("/general")
public class GeneralController {

    @Autowired
    private PostService postService;

    @GetMapping("/posts")
    public String listPosts(Model model) {
        model.addAttribute("posts", postService.findAllPosts());
        model.addAttribute("postForm", new PostForm());
        return "postsList";
    }

    @PostMapping("/posts")
    public String addPost() {
        return null;
    }
}