package com.acme.app.service;

import com.acme.app.domain.Post;
import com.acme.app.domain.Tag;
import com.acme.app.domain.User;
import com.acme.app.dto.PostDto;
import com.acme.app.repository.PostRepository;
import com.acme.app.repository.TagRepository;
import com.acme.app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
    }

    public List<Post> findAll() { return postRepository.findAll(); }

    public Optional<Post> findById(Long id) { return postRepository.findById(id); }

    public Optional<Post> findBySlug(String slug) { return postRepository.findBySlug(slug); }

    public Optional<Post> create(PostDto dto) {
        Optional<User> author = userRepository.findById(dto.getAuthorId());
        if (author.isEmpty()) return Optional.empty();
        Set<Tag> tags = new HashSet<>();
        if (dto.getTagIds() != null) {
            dto.getTagIds().forEach(tid -> tagRepository.findById(tid).ifPresent(tags::add));
        }
        Post p = new Post();
        p.setTitle(dto.getTitle());
        p.setSlug(dto.getSlug());
        p.setContent(dto.getContent());
        p.setExcerpt(dto.getExcerpt());
        p.setCoverImage(dto.getCoverImage());
        p.setAuthor(author.get());
        p.setTags(tags);
        return Optional.of(postRepository.save(p));
    }

    public Optional<Post> update(Long id, PostDto dto) {
        return postRepository.findById(id).map(p -> {
            p.setTitle(dto.getTitle());
            p.setSlug(dto.getSlug());
            p.setContent(dto.getContent());
            p.setExcerpt(dto.getExcerpt());
            p.setCoverImage(dto.getCoverImage());
            if (dto.getTagIds() != null) {
                Set<Tag> tags = new HashSet<>();
                dto.getTagIds().forEach(tid -> tagRepository.findById(tid).ifPresent(tags::add));
                p.setTags(tags);
            }
            p.setUpdatedAt(Instant.now());
            return p;
        });
    }

    public void delete(Long id) { postRepository.deleteById(id); }
}


