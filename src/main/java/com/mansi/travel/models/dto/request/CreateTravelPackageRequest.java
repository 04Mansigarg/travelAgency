package com.mansi.travel.models.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTravelPackageRequest {
    private String packageName;
    private Long packageCapacity;
    private List<DestinationRequest> destinations;
}
