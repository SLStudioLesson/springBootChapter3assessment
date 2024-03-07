package com.example.chapter03test.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.chapter03test.model.Post;
import com.example.chapter03test.model.User;
import com.example.chapter03test.repository.PostRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class PostService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PostRepository postRepository;

    public List<Post> findAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public Post createPost(String content, String imageUrl, User user) {
        Post newPost = new Post(content, imageUrl, user);
        return postRepository.save(newPost);
    }

    @Transactional
    public void updatePostImage(Long postId, String imageUrl) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + postId));
        post.setImageUrl(imageUrl);
        postRepository.save(post);
    }

}