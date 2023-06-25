package com.mansi.travel.models.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DestinationResponse {
    private String name;
    private List<ActivityResponse> activities;
}
