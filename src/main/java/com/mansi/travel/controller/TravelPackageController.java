package com.mansi.travel.controller;

import com.mansi.travel.models.dto.request.ActivityBookingRequest;
import com.mansi.travel.models.dto.request.CreatePassengerBooking;
import com.mansi.travel.models.dto.request.CreateTravelPackageRequest;
import com.mansi.travel.models.dto.response.ActivityResponse;
import com.mansi.travel.models.dto.response.PassengerResponse;
import com.mansi.travel.models.dto.response.TravelPackageDetails;
import com.mansi.travel.service.ActivityService;
import com.mansi.travel.service.PassengerService;
import com.mansi.travel.service.TravelService;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.LazyToOne;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@ComponentScan
public class TravelPackageController {

    private final TravelService travelService;
    private final PassengerService passengerService;
    private final ActivityService activityService;

    @PostMapping("/travel-package")
    public void createTravelPackage(@RequestBody CreateTravelPackageRequest request) {
       travelService.createTravelPackage(request);
    }

    @PostMapping("/passenger-booking")
    public void createBooking(@RequestBody CreatePassengerBooking request) {
        passengerService.createPassengerBooking(request);

    }

    @PostMapping("/passenger/{passengerId}/activity-booking")
    public String createActivityBooking(@PathVariable Long passengerId,
                                      @RequestBody ActivityBookingRequest request) {
        return activityService.createActivityBooking(passengerId, request);
    }

    @GetMapping("/travel-package")
    public List<TravelPackageDetails> getTravelPackageDetails() {
        return travelService.getTravelPackageDetails();
    }

    @GetMapping("/travel-package/passengers")
    public List<TravelPackageDetails> getTravelPackagePassengerDetails() {
        return travelService.getTravelPackagePassengerDetails();
    }

    @GetMapping("/passengers/activities-details")
    private List<PassengerResponse> getPassengerActivitiesDetails() {
        return passengerService.getPassengerActivityDetails();
    }

    @GetMapping("activity/available")
    public List<ActivityResponse> getAvailableActivities() {
        return activityService.getAvailableActivities();
    }
}
