package com.mansi.travel.models.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TravelPackageDetails {
    private String packageName;
    private Long packageCapacity;
    private Long passengerEnrolled;
    private List<PassengerResponse> passengers;
    private List<DestinationResponse> destinations;
}
