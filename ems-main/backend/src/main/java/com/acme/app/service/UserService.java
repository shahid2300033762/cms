package com.acme.app.service;

import com.acme.app.domain.User;
import com.acme.app.dto.UserDto;
import com.acme.app.domain.LoginAudit;
import com.acme.app.repository.UserRepository;
import com.acme.app.repository.LoginAuditRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final LoginAuditRepository loginAuditRepository;
    // Instantiate encoder on demand to avoid early class loading issues

    public UserService(UserRepository userRepository, LoginAuditRepository loginAuditRepository) {
        this.userRepository = userRepository;
        this.loginAuditRepository = loginAuditRepository;
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
        // Pre-check for existing email to return a friendly error before hitting DB constraint
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash(new BCryptPasswordEncoder().encode(rawPassword));
        return userRepository.save(user);
    }

    public Optional<User> authenticate(String email, String rawPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Optional<User> found = userRepository.findByEmail(email)
                .filter(u -> u.getPasswordHash() != null && encoder.matches(rawPassword, u.getPasswordHash()));
        found.ifPresent(u -> {
            u.setLastLoginAt(Instant.now());
            userRepository.save(u);
            LoginAudit audit = new LoginAudit();
            audit.setUser(u);
            audit.setEmail(u.getEmail());
            audit.setName(u.getName());
            audit.setLoginAt(Instant.now());
            audit.setSuccess(true);
            loginAuditRepository.save(audit);
        });
        return found;
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


