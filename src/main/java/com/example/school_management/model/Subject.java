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
@Document(collection = "subjects")
public class Subject {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("subject_code")
    private String subjectCode;

    private String name;

    private Integer credits;

    private String department;
}