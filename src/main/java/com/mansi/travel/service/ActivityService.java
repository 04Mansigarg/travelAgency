package com.mansi.travel.service;

import com.mansi.travel.models.domains.ActivityDao;
import com.mansi.travel.models.domains.DestinationDao;
import com.mansi.travel.models.domains.PassengerActivityDetailsMappingDao;
import com.mansi.travel.models.domains.PassengerDao;
import com.mansi.travel.models.domains.TravelPackageDao;
import com.mansi.travel.models.dto.request.ActivityBookingRequest;
import com.mansi.travel.models.dto.request.ActivityRequest;
import com.mansi.travel.models.dto.response.ActivityResponse;
import com.mansi.travel.models.enums.PassengerType;
import com.mansi.travel.repositories.ActivityRepository;
import com.mansi.travel.repositories.PassengerActivityDetailsMappingRepository;
import com.mansi.travel.repositories.PassengerRepository;
import com.mansi.travel.repositories.TravelPackageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Component
@Slf4j
@RequiredArgsConstructor
public class ActivityService {
    private final PassengerRepository passengerRepository;
    private final ActivityRepository activityRepository;
    private final TravelPackageRepository travelPackageRepository;
    private final PassengerActivityDetailsMappingRepository passengerActivityDetailsMappingRepository;

    // 4. Print the details of all the activities that still have spaces available, including how many spaces are available.

    public List<ActivityResponse> getAvailableActivities() {
        List<ActivityDao> activities = activityRepository.findAllAvailableActivities();
        return activities.stream().map(activity -> {
            return ActivityResponse.builder()
                    .description(activity.getDescription())
                    .name(activity.getName())
                    .cost(activity.getCost())
                    .capacity(activity.getCapacity())
                    .availableSlots(activity.getCapacity() - activity.getConsumption())
                    .build();
        }).toList();
    }

    public List<ActivityDao> getAllActivities(List<Long> destinationIds) {
        return activityRepository.findAllByDestinationIdIn(destinationIds);
    }

    public List<ActivityDao> buildAndSaveActivities(List<ActivityRequest> activities, DestinationDao destination) {
        if (CollectionUtils.isEmpty(activities)) {
            return Collections.emptyList();
        }
        List<ActivityDao> activityList = new ArrayList<>();
        activities.forEach(activityRequest -> {
            activityList.add(ActivityDao.builder()
                    .name(activityRequest.getName())
                    .cost(activityRequest.getCost())
                    .capacity(activityRequest.getCapacity())
                    .consumption(0L)
                    .description(activityRequest.getDescription())
                    .destination(destination)
                    .build());
        });
        return activityRepository.saveAll(activityList);
    }

    @Transactional
    public String createActivityBooking(Long passengerId, ActivityBookingRequest request) {
        TravelPackageDao travelPackageDao = travelPackageRepository.findById(request.getTravelPackageId())
                .orElseThrow(() -> new RuntimeException("Invalid Travel Package Id"));
        PassengerDao passengerDao = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new RuntimeException("Invalid Travel Package Id"));
        ActivityDao activityDao = activityRepository.findById(request.getActivityId())
                .orElseThrow(() -> new RuntimeException("Invalid Travel Package Id"));
        if (activityDao.getCapacity() - activityDao.getConsumption() > 0) {
            if (PassengerType.PREMIUM.equals(passengerDao.getPassengerType())) {
                buildAndSavePassengerActivityMapping(travelPackageDao, passengerDao, activityDao, 0d);
            }
            if (PassengerType.GOLD.equals(passengerDao.getPassengerType())) {
                Double costForPassenger = getActivityCostForPassenger(activityDao);
                savePassengerActivityDetails(passengerDao, costForPassenger);
                buildAndSavePassengerActivityMapping(travelPackageDao, passengerDao, activityDao, costForPassenger);
            }
            if (PassengerType.STANDARD.equals(passengerDao.getPassengerType())) {
                savePassengerActivityDetails(passengerDao, activityDao.getCost());
                buildAndSavePassengerActivityMapping(travelPackageDao, passengerDao, activityDao, activityDao.getCost());
            }
            activityDao.setConsumption(activityDao.getConsumption() + 1);
        } else {
            throw new RuntimeException("Seats full");
        }

      return String.format("Enrolled in Activity %s", activityDao.getName());
    }

    private void savePassengerActivityDetails(PassengerDao passengerDao, Double costForPassenger) {
        if (passengerDao.getRemainingBalance() >= costForPassenger) {
            passengerDao.setRemainingBalance(passengerDao.getRemainingBalance() - costForPassenger);
            passengerDao.setUtilizedBalance(passengerDao.getUtilizedBalance() + costForPassenger);
            passengerRepository.save(passengerDao);
        } else {
            throw new RuntimeException("Insufficient Balance");
        }
    }

    private void buildAndSavePassengerActivityMapping(TravelPackageDao travelPackageDao,
                                                      PassengerDao passengerDao,
                                                      ActivityDao activityDao,
                                                      Double activityCost) {
        PassengerActivityDetailsMappingDao mapping = PassengerActivityDetailsMappingDao.builder()
                .passenger(passengerDao)
                .activity(activityDao)
                .travelPackage(travelPackageDao)
                .destination(activityDao.getDestination())
                .activityCost(activityCost)
                .build();
        passengerActivityDetailsMappingRepository.save(mapping);
    }

    private Double getActivityCostForPassenger(ActivityDao activityDao) {
        BigDecimal actualCost = BigDecimal.valueOf(activityDao.getCost());
        BigDecimal discount = actualCost.divide(BigDecimal.TEN);
        return actualCost.subtract(discount).doubleValue();
    }
}
