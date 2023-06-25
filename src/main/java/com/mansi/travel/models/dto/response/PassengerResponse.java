package com.mansi.travel.models.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PassengerResponse {
    private String name;
    private String mobile;
    private Double remainingBalance;
    private List<PassengerActivityResponse> passengerActivities;
}
