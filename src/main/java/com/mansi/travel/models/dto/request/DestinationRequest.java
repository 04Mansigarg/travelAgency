package com.mansi.travel.models.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DestinationRequest {
    private String name;
    private List<ActivityRequest> activities;
}
