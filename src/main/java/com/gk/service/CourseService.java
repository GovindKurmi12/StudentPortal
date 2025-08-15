package com.gk.service;

import com.gk.model.Course;
import com.gk.model.Student;
import com.gk.repository.CourseRepository;
import com.gk.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    public CourseService(CourseRepository courseRepository, StudentRepository studentRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
    }

    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    public void deleteCourse(Long id) {
        Course course = getCourseById(id);
        courseRepository.delete(course);
    }

    public List<Course> searchCourses(String query) {
        return courseRepository.findByNameContainingIgnoreCase(query);
    }

    @Transactional
    public void enrollStudent(Long courseId, Long studentId) {
        Course course = getCourseById(courseId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));

        course.getStudents().add(student);
        student.getCourses().add(course);

        courseRepository.save(course);
        studentRepository.save(student);
    }

    @Transactional
    public void unenrollStudent(Long courseId, Long studentId) {
        Course course = getCourseById(courseId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));

        course.getStudents().remove(student);
        student.getCourses().remove(course);

        courseRepository.save(course);
        studentRepository.save(student);
    }

    public long getEnrollmentCount(Long courseId) {
        return courseRepository.getEnrollmentCount(courseId);
    }

    public void addSchedule(Long courseId, Course.Schedule schedule) {
        Course course = getCourseById(courseId);
        course.getSchedules().add(schedule);
        courseRepository.save(course);
    }

    public void removeSchedule(Long courseId, int index) {
        Course course = getCourseById(courseId);
        if (index >= 0 && index < course.getSchedules().size()) {
            course.getSchedules().remove(index);
            courseRepository.save(course);
        } else {
            throw new RuntimeException("Invalid schedule index");
        }
    }

    public List<Course> findByInstructor(String instructor) {
        return courseRepository.findByInstructor(instructor);
    }

    public List<Course> findAvailableCourses(int capacity) {
        return courseRepository.findAvailableCourses(capacity);
    }

    public Course findByCode(String code) {
        return courseRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Course not found with code: " + code));
    }

    public List<Course> findByMinimumCredits(int minCredits) {
        return courseRepository.findByMinimumCredits(minCredits);
    }
}
