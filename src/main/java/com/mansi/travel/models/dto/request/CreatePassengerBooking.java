package com.mansi.travel.models.dto.request;

import com.mansi.travel.models.enums.PassengerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePassengerBooking {

    private String name;

    @Min(10)
    @Max(12)
    private String mobile;

    private PassengerType passengerType;

    private Double totalBalance;

    private Long travelPackageId;

}
