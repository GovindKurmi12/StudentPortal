package com.gk.repository;

import com.gk.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCode(String code);

    List<Course> findByNameContainingIgnoreCase(String name);

    @Query("SELECT c FROM Course c JOIN c.schedules s WHERE s.instructor = :instructor")
    List<Course> findByInstructor(@Param("instructor") String instructor);

    @Query("SELECT c FROM Course c WHERE c.credits >= :minCredits")
    List<Course> findByMinimumCredits(@Param("minCredits") int minCredits);

    @Query("SELECT c FROM Course c WHERE SIZE(c.students) < :capacity")
    List<Course> findAvailableCourses(@Param("capacity") int capacity);

    @Query("SELECT COUNT(s) FROM Course c JOIN c.students s WHERE c.id = :courseId")
    long getEnrollmentCount(@Param("courseId") Long courseId);
}
