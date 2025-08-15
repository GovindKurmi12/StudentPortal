package com.gk.controller;

import com.gk.model.EventType;
import com.gk.model.Student;
import com.gk.model.StudentEvent;
import com.gk.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/events")
public class EventController {
    private final StudentService studentService;

    @Autowired
    public EventController(StudentService studentService) {
        this.studentService = studentService;
    }

    private String getEventClassName(EventType type) {
        return switch (type) {
            case EXAM -> "event-exam";
            case ASSIGNMENT -> "event-assignment";
            case MEETING -> "event-meeting";
            case ACTIVITY -> "event-activity";
            case HOLIDAY -> "event-holiday";
            case OTHER -> "event-other";
        };
    }

    @GetMapping("/calendar")
    public String showCalendar(Model model) {
        List<Student> students = studentService.getAllStudents();

        List<Map<String, Object>> events = students.stream()
            .flatMap(student -> student.getEvents().stream())
            .map(event -> {
                Map<String, Object> eventMap = new HashMap<>();
                eventMap.put("id", event.getId());
                eventMap.put("title", event.getTitle());
                eventMap.put("start", event.getStart().toString());
                eventMap.put("description", event.getDescription());
                eventMap.put("type", event.getType().toString());
                eventMap.put("className", getEventClassName(event.getType()));
                return eventMap;
            })
            .collect(Collectors.toList());

        model.addAttribute("events", events);
        model.addAttribute("eventTypes", EventType.values());
        return "events/calendar";
    }

    @GetMapping("/create")
    public String showEventForm(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("event", new StudentEvent());
        model.addAttribute("eventTypes", EventType.values());
        return "events/form";
    }

    @PostMapping("/save")
    public String saveEvent(
            @RequestParam Long studentId,
            @RequestParam String title,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam EventType type,
            @RequestParam String description) {

        StudentEvent event = StudentEvent.builder()
            .title(title)
            .start(start)
            .type(type)
            .description(description)
            .status("SCHEDULED")
            .build();

        studentService.addEventToStudent(studentId, event);
        return "redirect:/events/calendar";
    }

    @GetMapping("/{studentId}")
    public String getStudentEvents(@PathVariable Long studentId, Model model) {
        Student student = studentService.getStudentById(studentId);
        model.addAttribute("student", student);
        model.addAttribute("events", student.getEvents());
        model.addAttribute("eventTypes", EventType.values());
        return "events/student-events";
    }

    @DeleteMapping("/{studentId}/events/{eventId}")
    @ResponseBody
    public ResponseEntity<?> deleteEvent(@PathVariable Long studentId, @PathVariable Long eventId) {
        try {
            studentService.removeEventFromStudent(studentId, eventId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/upcoming")
    public String getUpcomingEvents(Model model) {
        model.addAttribute("upcomingEvents", studentService.getUpcomingEvents());
        return "events/upcoming";
    }
}
