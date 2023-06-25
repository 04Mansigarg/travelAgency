package com.mansi.travel.models.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityBookingRequest {
    @NotNull
    private Long activityId;

    @NotNull
    private Long travelPackageId;
}
