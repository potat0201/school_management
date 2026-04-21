package com.example.school_management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Field("password_hash")
    private String passwordHash;

    // Phân quyền: "ROLE_STUDENT", "ROLE_TEACHER", "ROLE_ADMIN"
    private String role;

    @Field("is_active")
    @Builder.Default
    private Boolean isActive = true;

    // ID trỏ về hồ sơ thật (Students hoặc Teachers)
    @Field("profile_id")
    private String profileId;

    @Field("created_at")
    @Builder.Default
    private Date createdAt = new Date();
}