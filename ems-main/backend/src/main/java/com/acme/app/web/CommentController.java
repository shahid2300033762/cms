package com.acme.app.web;

import com.acme.app.domain.Comment;
import com.acme.app.dto.CommentDto;
import com.acme.app.dto.CommentViewDto;
import com.acme.app.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) { this.commentService = commentService; }

    @GetMapping
    public List<CommentViewDto> list() {
        return commentService.findAll().stream().map(this::toView).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentViewDto> get(@PathVariable Long id) {
        return commentService.findById(id).map(c -> ResponseEntity.ok(toView(c))).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CommentViewDto> create(@Valid @RequestBody CommentDto dto) {
        return commentService.create(dto)
                .map(c -> ResponseEntity.created(URI.create("/api/comments/" + c.getId())).body(toView(c)))
                .orElse(ResponseEntity.unprocessableEntity().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentViewDto> update(@PathVariable Long id, @Valid @RequestBody CommentDto dto) {
        return commentService.update(id, dto).map(c -> ResponseEntity.ok(toView(c))).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private CommentViewDto toView(Comment c) {
        CommentViewDto v = new CommentViewDto();
        v.setId(c.getId());
        v.setPostId(c.getPost().getId());
        v.setUserId(c.getUser().getId());
        v.setUserName(c.getUser().getName());
        v.setUserAvatar(c.getUser().getAvatar());
        v.setContent(c.getContent());
        v.setCreatedAt(c.getCreatedAt());
        return v;
    }
}


