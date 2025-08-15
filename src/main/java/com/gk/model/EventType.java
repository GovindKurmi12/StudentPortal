package com.gk.model;

public enum EventType {
    EXAM("Exam", "exam-type", true, 180),
    ASSIGNMENT("Assignment", "assignment-type", true, 0),
    MEETING("Meeting", "meeting-type", false, 60),
    ACTIVITY("Activity", "activity-type", false, 120),
    HOLIDAY("Holiday", "holiday-type", false, 1440),
    OTHER("Other", "other-type", false, 60);

    private final String displayName;
    private final String cssClass;
    private final boolean requiresGrading;
    private final int defaultDurationMinutes;

    EventType(String displayName, String cssClass, boolean requiresGrading, int defaultDurationMinutes) {
        this.displayName = displayName;
        this.cssClass = cssClass;
        this.requiresGrading = requiresGrading;
        this.defaultDurationMinutes = defaultDurationMinutes;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCssClass() {
        return cssClass;
    }

    public boolean isRequiresGrading() {
        return requiresGrading;
    }

    public int getDefaultDurationMinutes() {
        return defaultDurationMinutes;
    }

    public boolean isAcademic() {
        return this == EXAM || this == ASSIGNMENT;
    }

    public boolean isAllDayEvent() {
        return this == HOLIDAY;
    }

    public String getIcon() {
        return switch (this) {
            case EXAM -> "📝";
            case ASSIGNMENT -> "📚";
            case MEETING -> "👥";
            case ACTIVITY -> "🎯";
            case HOLIDAY -> "🎉";
            case OTHER -> "📌";
        };
    }

    public String getDescription() {
        return switch (this) {
            case EXAM -> "An assessment to evaluate student knowledge";
            case ASSIGNMENT -> "A task or project to be completed";
            case MEETING -> "A scheduled discussion or consultation";
            case ACTIVITY -> "An extracurricular or co-curricular activity";
            case HOLIDAY -> "A non-working day or celebration";
            case OTHER -> "Miscellaneous event";
        };
    }
}
