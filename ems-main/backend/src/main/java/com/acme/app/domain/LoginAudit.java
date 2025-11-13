package com.acme.app.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "login_audit")
public class LoginAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(length = 100)
    private String name;

    @Column(name = "login_at", nullable = false)
    private Instant loginAt = Instant.now();

    @Column(nullable = false)
    private boolean success;

    public LoginAudit() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Instant getLoginAt() { return loginAt; }
    public void setLoginAt(Instant loginAt) { this.loginAt = loginAt; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}