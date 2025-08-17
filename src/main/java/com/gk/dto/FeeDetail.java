package com.gk.dto;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.Date;

@Embeddable
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FeeDetail {
    @NotNull
    private String feeType;

    @NotNull
    @Positive
    private Double amount;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date paidDate;

    @NotNull
    private String status;

    @NotNull
    private String transactionId;

    private String remarks;
}