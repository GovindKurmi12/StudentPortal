package com.gk.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.util.*;

@Entity
@Table(name = "courses")
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

    public Course() {}

    public Course(Long id, String name, String code, String description, int credits, double fee,
                 Set<Student> students, List<Schedule> schedules, Date startDate, Date endDate,
                 int maxStudents, Set<String> prerequisites, String department, boolean active,
                 String syllabus, List<Material> materials, List<Assessment> assessments) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.credits = credits;
        this.fee = fee;
        this.students = students != null ? students : new HashSet<>();
        this.schedules = schedules != null ? schedules : new ArrayList<>();
        this.startDate = startDate;
        this.endDate = endDate;
        this.maxStudents = maxStudents;
        this.prerequisites = prerequisites != null ? prerequisites : new HashSet<>();
        this.department = department;
        this.active = active;
        this.syllabus = syllabus;
        this.materials = materials != null ? materials : new ArrayList<>();
        this.assessments = assessments != null ? assessments : new ArrayList<>();
    }

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

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public Set<Student> getStudents() {
        return students;
    }

    public void setStudents(Set<Student> students) {
        this.students = students != null ? students : new HashSet<>();
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules != null ? schedules : new ArrayList<>();
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getMaxStudents() {
        return maxStudents;
    }

    public void setMaxStudents(int maxStudents) {
        this.maxStudents = maxStudents;
    }

    public Set<String> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(Set<String> prerequisites) {
        this.prerequisites = prerequisites != null ? prerequisites : new HashSet<>();
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public boolean isActive() {
        if (!active) return false;
        Date now = new Date();
        return (startDate == null || !now.before(startDate)) &&
               (endDate == null || !now.after(endDate));
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getSyllabus() {
        return syllabus;
    }

    public void setSyllabus(String syllabus) {
        this.syllabus = syllabus;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials != null ? materials : new ArrayList<>();
    }

    public List<Assessment> getAssessments() {
        return assessments;
    }

    public void setAssessments(List<Assessment> assessments) {
        this.assessments = assessments != null ? assessments : new ArrayList<>();
    }

    public int getTotalUnits() {
        return credits * 15; // Assuming each credit equals 15 units of work/study
    }

    // Helper methods
    public boolean hasVacancy() {
        return students.size() < maxStudents;
    }

    public boolean isStudentEnrolled(Student student) {
        return students.contains(student);
    }

    public String getFormattedSchedule() {
        StringBuilder sb = new StringBuilder();
        for (Schedule schedule : schedules) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(schedule.toString());
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return credits == course.credits &&
               Double.compare(course.fee, fee) == 0 &&
               maxStudents == course.maxStudents &&
               active == course.active &&
               Objects.equals(id, course.id) &&
               Objects.equals(name, course.name) &&
               Objects.equals(code, course.code) &&
               Objects.equals(description, course.description) &&
               Objects.equals(startDate, course.startDate) &&
               Objects.equals(endDate, course.endDate) &&
               Objects.equals(department, course.department) &&
               Objects.equals(syllabus, course.syllabus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, code, description, credits, fee, startDate,
                          endDate, maxStudents, department, active, syllabus);
    }

    @Override
    public String toString() {
        return "Course{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", code='" + code + '\'' +
               ", description='" + description + '\'' +
               ", credits=" + credits +
               ", fee=" + fee +
               ", maxStudents=" + maxStudents +
               ", department='" + department + '\'' +
               ", active=" + active +
               ", startDate=" + startDate +
               ", endDate=" + endDate +
               '}';
    }

    public static CourseBuilder builder() {
        return new CourseBuilder();
    }

    public static class CourseBuilder {
        private Long id;
        private String name;
        private String code;
        private String description;
        private int credits;
        private double fee;
        private Set<Student> students = new HashSet<>();
        private List<Schedule> schedules = new ArrayList<>();
        private Date startDate;
        private Date endDate;
        private int maxStudents;
        private Set<String> prerequisites = new HashSet<>();
        private String department;
        private boolean active = true;
        private String syllabus;
        private List<Material> materials = new ArrayList<>();
        private List<Assessment> assessments = new ArrayList<>();

        CourseBuilder() {}

        public CourseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CourseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public CourseBuilder code(String code) {
            this.code = code;
            return this;
        }

        public CourseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public CourseBuilder credits(int credits) {
            this.credits = credits;
            return this;
        }

        public CourseBuilder fee(double fee) {
            this.fee = fee;
            return this;
        }

        public CourseBuilder students(Set<Student> students) {
            this.students = students;
            return this;
        }

        public CourseBuilder schedules(List<Schedule> schedules) {
            this.schedules = schedules;
            return this;
        }

        public CourseBuilder startDate(Date startDate) {
            this.startDate = startDate;
            return this;
        }

        public CourseBuilder endDate(Date endDate) {
            this.endDate = endDate;
            return this;
        }

        public CourseBuilder maxStudents(int maxStudents) {
            this.maxStudents = maxStudents;
            return this;
        }

        public CourseBuilder prerequisites(Set<String> prerequisites) {
            this.prerequisites = prerequisites;
            return this;
        }

        public CourseBuilder department(String department) {
            this.department = department;
            return this;
        }

        public CourseBuilder active(boolean active) {
            this.active = active;
            return this;
        }

        public CourseBuilder syllabus(String syllabus) {
            this.syllabus = syllabus;
            return this;
        }

        public CourseBuilder materials(List<Material> materials) {
            this.materials = materials;
            return this;
        }

        public CourseBuilder assessments(List<Assessment> assessments) {
            this.assessments = assessments;
            return this;
        }

        public Course build() {
            return new Course(id, name, code, description, credits, fee, students,
                            schedules, startDate, endDate, maxStudents, prerequisites,
                            department, active, syllabus, materials, assessments);
        }
    }

    @Embeddable
    public static class Schedule {
        @NotBlank(message = "Day is required")
        @Pattern(regexp = "^(MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY)$",
                message = "Invalid day format")
        private String day;

        @NotBlank(message = "Start time is required")
        @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$",
                message = "Start time must be in HH:mm format")
        private String startTime;

        @NotBlank(message = "End time is required")
        @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$",
                message = "End time must be in HH:mm format")
        private String endTime;

        @NotBlank(message = "Room number is required")
        @Pattern(regexp = "^[A-Z]\\d{3}$", message = "Room must be in format: A123")
        private String room;

        @NotBlank(message = "Instructor name is required")
        @Length(min = 2, max = 100, message = "Instructor name must be between 2 and 100 characters")
        private String instructor;

        @Column(name = "session_type")
        private String sessionType;

        @Column(name = "is_recurring")
        private boolean recurring = true;

        public Schedule() {}

        public Schedule(String day, String startTime, String endTime, String room,
                       String instructor, String sessionType, boolean recurring) {
            this.day = day;
            this.startTime = startTime;
            this.endTime = endTime;
            this.room = room;
            this.instructor = instructor;
            this.sessionType = sessionType;
            this.recurring = recurring;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getRoom() {
            return room;
        }

        public void setRoom(String room) {
            this.room = room;
        }

        public String getInstructor() {
            return instructor;
        }

        public void setInstructor(String instructor) {
            this.instructor = instructor;
        }

        public String getSessionType() {
            return sessionType;
        }

        public void setSessionType(String sessionType) {
            this.sessionType = sessionType;
        }

        public boolean isRecurring() {
            return recurring;
        }

        public void setRecurring(boolean recurring) {
            this.recurring = recurring;
        }

        public boolean conflictsWith(Schedule other) {
            if (!this.day.equals(other.day)) return false;
            return timeOverlaps(this.startTime, this.endTime,
                              other.startTime, other.endTime);
        }

        private boolean timeOverlaps(String start1, String end1,
                                   String start2, String end2) {
            return start1.compareTo(end2) < 0 && end1.compareTo(start2) > 0;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Schedule schedule = (Schedule) o;
            return recurring == schedule.recurring &&
                   Objects.equals(day, schedule.day) &&
                   Objects.equals(startTime, schedule.startTime) &&
                   Objects.equals(endTime, schedule.endTime) &&
                   Objects.equals(room, schedule.room) &&
                   Objects.equals(instructor, schedule.instructor) &&
                   Objects.equals(sessionType, schedule.sessionType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(day, startTime, endTime, room, instructor,
                              sessionType, recurring);
        }

        @Override
        public String toString() {
            return day + " " + startTime + "-" + endTime + " in " + room +
                   " by " + instructor + (sessionType != null ? " (" + sessionType + ")" : "");
        }
    }

    @Embeddable
    public static class Material {
        @NotBlank(message = "Title is required")
        private String title;

        @Length(max = 500)
        private String description;

        private String type;

        @Pattern(regexp = "^(http|https)://.*$", message = "Must be a valid URL")
        private String url;

        public Material() {}

        public Material(String title, String description, String type, String url) {
            this.title = title;
            this.description = description;
            this.type = type;
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Material material = (Material) o;
            return Objects.equals(title, material.title) &&
                   Objects.equals(description, material.description) &&
                   Objects.equals(type, material.type) &&
                   Objects.equals(url, material.url);
        }

        @Override
        public int hashCode() {
            return Objects.hash(title, description, type, url);
        }

        @Override
        public String toString() {
            return "Material{" +
                   "title='" + title + '\'' +
                   ", description='" + description + '\'' +
                   ", type='" + type + '\'' +
                   ", url='" + url + '\'' +
                   '}';
        }
    }

    @Embeddable
    public static class Assessment {
        @NotBlank(message = "Title is required")
        private String title;

        @DecimalMin(value = "0.0")
        @DecimalMax(value = "100.0")
        private double weightage;

        @Temporal(TemporalType.DATE)
        private Date dueDate;

        private String type;

        @Length(max = 500)
        private String description;

        public Assessment() {}

        public Assessment(String title, double weightage, Date dueDate,
                        String type, String description) {
            this.title = title;
            this.weightage = weightage;
            this.dueDate = dueDate;
            this.type = type;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public double getWeightage() {
            return weightage;
        }

        public void setWeightage(double weightage) {
            this.weightage = weightage;
        }

        public Date getDueDate() {
            return dueDate;
        }

        public void setDueDate(Date dueDate) {
            this.dueDate = dueDate;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Assessment that = (Assessment) o;
            return Double.compare(that.weightage, weightage) == 0 &&
                   Objects.equals(title, that.title) &&
                   Objects.equals(dueDate, that.dueDate) &&
                   Objects.equals(type, that.type) &&
                   Objects.equals(description, that.description);
        }

        @Override
        public int hashCode() {
            return Objects.hash(title, weightage, dueDate, type, description);
        }

        @Override
        public String toString() {
            return "Assessment{" +
                   "title='" + title + '\'' +
                   ", weightage=" + weightage +
                   ", dueDate=" + dueDate +
                   ", type='" + type + '\'' +
                   ", description='" + description + '\'' +
                   '}';
        }
    }
}
