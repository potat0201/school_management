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
@Document(collection = "semesters")
public class Semester {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("semester_code")
    private String semesterCode;

    private String name;

    @Field("start_date")
    private Date startDate;

    @Field("end_date")
    private Date endDate;

    @Field("is_registration_open")
    @Builder.Default
    private Boolean isRegistrationOpen = false;
}