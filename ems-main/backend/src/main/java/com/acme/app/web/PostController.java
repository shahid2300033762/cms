package com.acme.app.web;

import com.acme.app.domain.Post;
import com.acme.app.domain.Tag;
import com.acme.app.dto.PostViewDto;
import com.acme.app.dto.UserViewDto;
import com.acme.app.dto.PostDto;
import com.acme.app.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) { this.postService = postService; }

    @GetMapping
    public List<PostViewDto> list() {
        return postService.findAll().stream().map(this::toView).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostViewDto> get(@PathVariable Long id) {
        return postService.findById(id).map(p -> ResponseEntity.ok(toView(p))).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<PostViewDto> getBySlug(@PathVariable String slug) {
        return postService.findBySlug(slug).map(p -> ResponseEntity.ok(toView(p))).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PostViewDto> create(@Valid @RequestBody PostDto dto) {
        return postService.create(dto)
                .map(p -> ResponseEntity.created(URI.create("/api/posts/" + p.getId())).body(toView(p)))
                .orElse(ResponseEntity.unprocessableEntity().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostViewDto> update(@PathVariable Long id, @Valid @RequestBody PostDto dto) {
        return postService.update(id, dto).map(p -> ResponseEntity.ok(toView(p))).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private PostViewDto toView(Post p) {
        PostViewDto v = new PostViewDto();
        v.setId(p.getId());
        v.setTitle(p.getTitle());
        v.setSlug(p.getSlug());
        v.setExcerpt(p.getExcerpt());
        v.setContent(p.getContent());
        v.setCoverImage(p.getCoverImage());
        v.setAuthor(new UserViewDto(p.getAuthor().getId(), p.getAuthor().getName(), p.getAuthor().getAvatar()));
        v.setTags(p.getTags().stream().map(Tag::getName).toList());
        v.setCreatedAt(p.getCreatedAt());
        v.setUpdatedAt(p.getUpdatedAt());
        v.setFeatured(p.isFeatured());
        v.setLikes(p.getLikes());
        v.setCommentCount(p.getCommentCount());
        v.setReadTime(p.getReadTime());
        return v;
    }
}


