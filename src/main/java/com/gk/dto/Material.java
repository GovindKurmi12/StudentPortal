package com.gk.dto;

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
public class Material {
    @NotBlank(message = "Title is required")
    private String title;

    @Length(max = 500)
    private String description;

    private String type;

    @Pattern(regexp = "^(http|https)://.*$", message = "Must be a valid URL")
    private String url;
}