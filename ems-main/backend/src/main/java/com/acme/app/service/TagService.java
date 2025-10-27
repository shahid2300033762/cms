package com.acme.app.service;

import com.acme.app.domain.Tag;
import com.acme.app.dto.TagDto;
import com.acme.app.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<Tag> findAll() { return tagRepository.findAll(); }

    public Optional<Tag> findById(Long id) { return tagRepository.findById(id); }

    public Tag create(TagDto dto) {
        Tag t = new Tag();
        t.setName(dto.getName());
        return tagRepository.save(t);
    }

    public Optional<Tag> update(Long id, TagDto dto) {
        return tagRepository.findById(id).map(t -> { t.setName(dto.getName()); return t; });
    }

    public void delete(Long id) { tagRepository.deleteById(id); }
}


