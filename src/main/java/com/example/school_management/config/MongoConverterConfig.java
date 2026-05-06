package com.example.school_management.config;

import com.example.school_management.model.Classroom;
import org.bson.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom converters to handle existing MongoDB data where schedules/students
 * were stored as nested arrays [[{...}]] instead of flat arrays [{...}].
 */
@Configuration
public class MongoConverterConfig {

    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(List.of(
                new ScheduleReadingConverter(),
                new StudentSummaryReadingConverter()
        ));
    }

    @ReadingConverter
    public static class ScheduleReadingConverter implements Converter<ArrayList, Classroom.Schedule> {
        @Override
        public Classroom.Schedule convert(ArrayList source) {
            if (source == null || source.isEmpty()) return null;
            Object first = source.get(0);
            if (first instanceof Document doc) {
                return new Classroom.Schedule(
                        (String) doc.get("day_of_week"),
                        (String) doc.get("time_slot"),
                        (String) doc.get("room")
                );
            }
            return null;
        }
    }

    @ReadingConverter
    public static class StudentSummaryReadingConverter implements Converter<ArrayList, Classroom.StudentSummary> {
        @Override
        public Classroom.StudentSummary convert(ArrayList source) {
            if (source == null || source.isEmpty()) return null;
            Object first = source.get(0);
            if (first instanceof Document doc) {
                return new Classroom.StudentSummary(
                        (String) doc.get("student_id"),
                        (String) doc.get("student_code"),
                        (String) doc.get("name")
                );
            }
            return null;
        }
    }
}
