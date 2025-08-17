package com.gk.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Embeddable
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
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

    private String dayOfWeek;

    @Column(name = "session_type")
    private String sessionType;

    @Column(name = "is_recurring")
    private boolean recurring = true;
}