package com.gk.dto;

import lombok.Getter;

@Getter
public enum AttendanceStatus {
    PRESENT("Present"),
    ABSENT("Absent"),
    LATE("Late"),
    EXCUSED("Excused");

    private final String displayName;

    AttendanceStatus(String displayName) {
        this.displayName = displayName;
    }
}