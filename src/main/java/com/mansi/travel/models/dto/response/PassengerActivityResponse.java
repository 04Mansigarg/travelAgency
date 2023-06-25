package com.mansi.travel.models.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PassengerActivityResponse {
    private String activityName;
    private Double amountPaidForActivity;
    private String destinationName;
    private String travelPackageName;
}
