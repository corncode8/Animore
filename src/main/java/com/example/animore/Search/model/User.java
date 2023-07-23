package com.example.animore.Search.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservation")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long userId;
    @Column(name="user_name")
    private String userName;
    @Column(name="password")
    private String password;
    @Column(name="email")
    private String email;
    @Column(name="phone")
    private String phone;
    @Column(name="address")
    private String address;
    @Column(name="role")
    private String role;
    @Column(name="provider")
    private String provider;
    @Column(name="provider_id")
    private String providerId;
    @CreationTimestamp
    @Column(name="create_at")
    private Timestamp createAt;
    @UpdateTimestamp
    @Column(name="update_at")
    private Timestamp updateAt;
}