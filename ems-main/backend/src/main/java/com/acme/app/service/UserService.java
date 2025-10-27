package com.acme.app.service;

import com.acme.app.domain.User;
import com.acme.app.dto.UserDto;
import com.acme.app.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    // Instantiate encoder on demand to avoid early class loading issues

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() { return userRepository.findAll(); }

    public Optional<User> findById(Long id) { return userRepository.findById(id); }

    public User create(UserDto dto) {
        User u = new User();
        u.setName(dto.getName());
        u.setEmail(dto.getEmail());
        u.setAvatar(dto.getAvatar());
        u.setBio(dto.getBio());
        return userRepository.save(u);
    }

    public User register(String name, String email, String rawPassword) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash(new BCryptPasswordEncoder().encode(rawPassword));
        return userRepository.save(user);
    }

    public Optional<User> authenticate(String email, String rawPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return userRepository.findByEmail(email)
                .filter(u -> u.getPasswordHash() != null && encoder.matches(rawPassword, u.getPasswordHash()));
    }

    public Optional<User> update(Long id, UserDto dto) {
        return userRepository.findById(id).map(u -> {
            u.setName(dto.getName());
            u.setEmail(dto.getEmail());
            u.setAvatar(dto.getAvatar());
            u.setBio(dto.getBio());
            return u;
        });
    }

    public void delete(Long id) { userRepository.deleteById(id); }
}


