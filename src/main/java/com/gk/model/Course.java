package com.gk.model;

import com.gk.dto.Assessment;
import com.gk.dto.Material;
import com.gk.dto.Schedule;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.*;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Course name is required")
    @Length(min = 2, max = 100, message = "Course name must be between 2 and 100 characters")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Course code is required")
    @Pattern(regexp = "^[A-Z]{2,5}\\d{3,4}$", message = "Course code must be 2-5 capital letters followed by 3-4 digits")
    @Column(unique = true)
    private String code;

    @Length(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Min(value = 1, message = "Credits must be at least 1")
    @Max(value = 6, message = "Credits cannot exceed 6")
    private int credits;

    @DecimalMin(value = "0.0", message = "Fee cannot be negative")
    private double fee;

    @ManyToMany(mappedBy = "courses")
    private Set<Student> students = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "course_schedules", joinColumns = @JoinColumn(name = "course_id"))
    private List<Schedule> schedules = new ArrayList<>();

    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(name = "max_students")
    @Min(value = 1, message = "Maximum students must be at least 1")
    private int maxStudents;

    @Column(name = "prerequisites")
    @ElementCollection
    private Set<String> prerequisites = new HashSet<>();

    @Column(name = "department")
    private String department;

    @Column(name = "is_active")
    private boolean active = true;

    @Column(name = "syllabus")
    @Length(max = 2000)
    private String syllabus;

    @ElementCollection
    @CollectionTable(name = "course_materials")
    private List<Material> materials = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "course_assessments")
    private List<Assessment> assessments = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCredits() {
        return credits;
    }

    public int getTotalUnits() {
        return credits * 15; // Assuming each credit equals 15 units of work/study
    }

    public String getInstructor() {
        if (schedules == null || schedules.isEmpty()) {
            return null;
        }
        Set<String> instructors = new HashSet<>();
        for (Schedule schedule : schedules) {
            instructors.add(schedule.getInstructor());
        }
        return String.join(", ", instructors);
    }
}
