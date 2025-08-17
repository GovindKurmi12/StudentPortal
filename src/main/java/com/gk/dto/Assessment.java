package com.gk.dto;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

@Embeddable
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Assessment {
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

}