package com.mansi.travel.models.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityRequest {
    private String name;
    private String description;
    private Double cost;
    private Long capacity;
}
