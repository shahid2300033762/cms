package com.acme.app.service;

import com.acme.app.domain.Comment;
import com.acme.app.domain.Post;
import com.acme.app.domain.User;
import com.acme.app.dto.CommentDto;
import com.acme.app.repository.CommentRepository;
import com.acme.app.repository.PostRepository;
import com.acme.app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public List<Comment> findAll() { return commentRepository.findAll(); }

    public Optional<Comment> findById(Long id) { return commentRepository.findById(id); }

    public Optional<Comment> create(CommentDto dto) {
        Optional<Post> post = postRepository.findById(dto.getPostId());
        Optional<User> user = userRepository.findById(dto.getUserId());
        if (post.isEmpty() || user.isEmpty()) return Optional.empty();
        Comment c = new Comment();
        c.setPost(post.get());
        c.setUser(user.get());
        c.setContent(dto.getContent());
        return Optional.of(commentRepository.save(c));
    }

    public Optional<Comment> update(Long id, CommentDto dto) {
        return commentRepository.findById(id).map(c -> { c.setContent(dto.getContent()); return c; });
    }

    public void delete(Long id) { commentRepository.deleteById(id); }
}


