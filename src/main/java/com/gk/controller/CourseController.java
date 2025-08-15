package com.gk.controller;

import com.gk.exception.NotFoundException;
import com.gk.model.Course;
import com.gk.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public String listCourses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        return "courses/list";
    }

    @GetMapping("/new")
    public String newCourseForm(Model model) {
        model.addAttribute("course", new Course());
        return "courses/form";
    }

    @PostMapping("/save")
    public String saveCourse(@Valid @ModelAttribute Course course,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "courses/form";
        }

        try {
            courseService.saveCourse(course);
            redirectAttributes.addFlashAttribute("message", "Course saved successfully!");
            return "redirect:/courses";
        } catch (Exception e) {
            result.rejectValue("code", "error.course", e.getMessage());
            return "courses/form";
        }
    }

    @GetMapping("/{id}")
    public String viewCourse(@PathVariable Long id, Model model) {
        try {
            Course course = courseService.getCourseById(id);
            model.addAttribute("course", course);
            model.addAttribute("enrolledStudents", course.getStudents());
            model.addAttribute("schedules", course.getSchedules());
            return "courses/view";
        } catch (Exception e) {
            throw new NotFoundException("Course not found with id: " + id);
        }
    }

    @GetMapping("/{id}/edit")
    public String editCourse(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("course", courseService.getCourseById(id));
            return "courses/form";
        } catch (Exception e) {
            throw new NotFoundException("Course not found with id: " + id);
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteCourse(id);
            redirectAttributes.addFlashAttribute("message", "Course deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/courses";
    }

    @GetMapping("/{id}/students")
    public String viewEnrolledStudents(@PathVariable Long id, Model model) {
        try {
            Course course = courseService.getCourseById(id);
            model.addAttribute("course", course);
            model.addAttribute("students", course.getStudents());
            model.addAttribute("enrollmentCount", courseService.getEnrollmentCount(id));
            return "courses/students";
        } catch (Exception e) {
            throw new NotFoundException("Course not found with id: " + id);
        }
    }

    @PostMapping("/{courseId}/enroll/{studentId}")
    public String enrollStudent(@PathVariable Long courseId,
                                @PathVariable Long studentId,
                                RedirectAttributes redirectAttributes) {
        try {
            courseService.enrollStudent(courseId, studentId);
            redirectAttributes.addFlashAttribute("message", "Student enrolled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/courses/" + courseId + "/students";
    }

    @PostMapping("/{courseId}/unenroll/{studentId}")
    public String unenrollStudent(@PathVariable Long courseId,
                                  @PathVariable Long studentId,
                                  RedirectAttributes redirectAttributes) {
        try {
            courseService.unenrollStudent(courseId, studentId);
            redirectAttributes.addFlashAttribute("message", "Student unenrolled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/courses/" + courseId + "/students";
    }

    @GetMapping("/search")
    public String searchCourses(@RequestParam String query, Model model) {
        model.addAttribute("courses", courseService.searchCourses(query));
        model.addAttribute("searchQuery", query);
        return "courses/list";
    }

    @GetMapping("/{id}/schedule")
    public String viewSchedule(@PathVariable Long id, Model model) {
        try {
            Course course = courseService.getCourseById(id);
            model.addAttribute("course", course);
            model.addAttribute("schedules", course.getSchedules());
            return "courses/schedule";
        } catch (Exception e) {
            throw new NotFoundException("Course not found with id: " + id);
        }
    }

    @PostMapping("/{id}/schedule/add")
    public String addSchedule(@PathVariable Long id,
                              @Valid @ModelAttribute Course.Schedule schedule,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Invalid schedule data");
            return "redirect:/courses/" + id + "/schedule";
        }

        try {
            courseService.addSchedule(id, schedule);
            redirectAttributes.addFlashAttribute("message", "Schedule added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/courses/" + id + "/schedule";
    }

    @PostMapping("/{courseId}/schedule/{index}/delete")
    public String deleteSchedule(@PathVariable Long courseId,
                                 @PathVariable int index,
                                 RedirectAttributes redirectAttributes) {
        try {
            courseService.removeSchedule(courseId, index);
            redirectAttributes.addFlashAttribute("message", "Schedule removed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/courses/" + courseId + "/schedule";
    }

    @GetMapping("/instructor/{instructor}")
    public String getCoursesByInstructor(@PathVariable String instructor, Model model) {
        model.addAttribute("courses", courseService.findByInstructor(instructor));
        model.addAttribute("instructor", instructor);
        return "courses/list";
    }
}
