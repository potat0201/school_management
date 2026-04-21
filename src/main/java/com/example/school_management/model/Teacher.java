package com.example.school_management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "teachers")
public class Teacher {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("teacher_code")
    private String teacherCode;

    private String name;

    @Indexed(unique = true)
    private String email;

    private String department;
}