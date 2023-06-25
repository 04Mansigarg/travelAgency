package com.mansi.travel.models.dto.response;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ActivityResponse {
    private String name;
    private String description;
    private Double cost;
    private Long capacity;
    private Long availableSlots;
}
