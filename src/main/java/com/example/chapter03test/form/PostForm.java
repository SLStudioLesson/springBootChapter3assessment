package com.example.chapter03test.form;

import org.springframework.web.multipart.MultipartFile;

public class PostForm {

    private String content;

    private MultipartFile image;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
