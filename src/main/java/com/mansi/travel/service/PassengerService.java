package com.mansi.travel.service;

import com.mansi.travel.models.domains.ActivityDao;
import com.mansi.travel.models.domains.BaseDao;
import com.mansi.travel.models.domains.DestinationDao;
import com.mansi.travel.models.domains.PassengerDao;
import com.mansi.travel.models.domains.PassengerActivityDetailsMappingDao;
import com.mansi.travel.models.domains.PassengerTravelMappingDao;
import com.mansi.travel.models.domains.TravelPackageDao;
import com.mansi.travel.models.dto.request.CreatePassengerBooking;
import com.mansi.travel.models.dto.response.PassengerActivityResponse;
import com.mansi.travel.models.dto.response.PassengerResponse;
import com.mansi.travel.models.enums.PassengerType;
import com.mansi.travel.repositories.ActivityRepository;
import com.mansi.travel.repositories.DestinationRepository;
import com.mansi.travel.repositories.PassengerRepository;
import com.mansi.travel.repositories.PassengerActivityDetailsMappingRepository;
import com.mansi.travel.repositories.PassengerTravelMappingRepository;
import com.mansi.travel.repositories.TravelPackageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@Service
@Component
@Slf4j
@RequiredArgsConstructor
public class PassengerService {

    private final PassengerRepository passengerRepository;
    private final PassengerActivityDetailsMappingRepository passengerActivityDetailsMappingRepository;
    private final TravelPackageRepository travelPackageRepository;
    private final ActivityRepository activityRepository;
    private final DestinationRepository destinationRepository;

    private final PassengerTravelMappingRepository passengerTravelMappingRepository;


//    3. Print the details of an individual passenger including their
//       a. name,
//       b. passenger number,
//       c. balance (if applicable),
//       d. list of each activity they have signed up for, including the destination the at which the activity is taking place and the price the passenger paid for the activity.


    public List<PassengerResponse> getPassengerActivityDetails() {
        List<PassengerDao> passengers = passengerRepository.findAll();
        Set<Long> passengerIds = getPassengerIds(passengers);
        List<PassengerActivityDetailsMappingDao> passengerActivityDetailsMapping = passengerActivityDetailsMappingRepository.findAllByPassengerIdIn(passengerIds);

        Map<Long, ActivityDao> activitiesMap = getActivityMap(passengerActivityDetailsMapping);
        Map<Long, TravelPackageDao> travelPackagesMap = getTravelPackageMap(passengerActivityDetailsMapping);
        Map<Long, DestinationDao> destinationsMap = getDestinationMap(passengerActivityDetailsMapping);

        Map<Long, List<PassengerActivityDetailsMappingDao>> mapOfPassengerIdToDetailMapping = passengerActivityDetailsMapping.stream()
                .collect(Collectors.groupingBy(mappingDetails -> mappingDetails.getPassenger().getId()));
        List<PassengerResponse> passengerResponses = new ArrayList<>();
        mapOfPassengerIdToDetailMapping.forEach((passengerId, mappingDetails) -> {
            PassengerDao passenger = passengers.stream().filter(p -> p.getId().equals(passengerId)).findFirst().get();
            passengerResponses.add(PassengerResponse.builder()
                    .name(passenger.getName())
                    .mobile(passenger.getMobile())
                    .remainingBalance(passenger.getRemainingBalance())
                    .passengerActivities(buildPassengerActivitiesResponse(mappingDetails, activitiesMap, travelPackagesMap, destinationsMap))
                    .build());
        });
        return passengerResponses;
    }

    private Map<Long, ActivityDao> getActivityMap(List<PassengerActivityDetailsMappingDao> passengerActivityDetailsMapping) {
        List<ActivityDao> activities = activityRepository.findAllById(getActivitiesId(passengerActivityDetailsMapping));
        return activities.stream()
                .collect(Collectors.toMap(ActivityDao::getId, Function.identity()));
    }

    private Map<Long, TravelPackageDao> getTravelPackageMap(List<PassengerActivityDetailsMappingDao> passengerActivityDetailsMapping) {
        List<TravelPackageDao> travelPackages = travelPackageRepository.findAllById(getTravelPackageIds(passengerActivityDetailsMapping));;
        return travelPackages.stream()
                .collect(Collectors.toMap(TravelPackageDao::getId, Function.identity()));
    }

    private Map<Long, DestinationDao> getDestinationMap(List<PassengerActivityDetailsMappingDao> passengerActivityDetailsMapping) {
        List<DestinationDao> destinations = destinationRepository.findAllById(getDestinationIds(passengerActivityDetailsMapping));
        return destinations.stream()
                .collect(Collectors.toMap(DestinationDao::getId, Function.identity()));
    }

    private List<PassengerActivityResponse> buildPassengerActivitiesResponse(List<PassengerActivityDetailsMappingDao> mappingDetails,
                                                                             Map<Long, ActivityDao> activitiesMap,
                                                                             Map<Long, TravelPackageDao> travelPackagesMap,
                                                                             Map<Long, DestinationDao> destinationsMap) {
        return mappingDetails.stream().map(mappingDetail -> {
            return PassengerActivityResponse.builder()
                    .activityName(activitiesMap.get(mappingDetail.getActivity().getId()).getName())
                    .amountPaidForActivity(mappingDetail.getActivityCost())
                    .destinationName(destinationsMap.get(mappingDetail.getDestination().getId()).getName())
                    .travelPackageName(travelPackagesMap.get(mappingDetail.getTravelPackage().getId()).getName())
                    .build();
        }).toList();
    }

    private static Set<Long> getDestinationIds(List<PassengerActivityDetailsMappingDao> passengerActivityDetailsMapping) {
        return passengerActivityDetailsMapping.stream().map(data -> data.getDestination().getId()).collect(Collectors.toSet());
    }

    private static Set<Long> getTravelPackageIds(List<PassengerActivityDetailsMappingDao> passengerActivityDetailsMapping) {
        return passengerActivityDetailsMapping.stream().map(data -> data.getTravelPackage().getId()).collect(Collectors.toSet());
    }

    private static Set<Long> getActivitiesId(List<PassengerActivityDetailsMappingDao> passengerActivityDetailsMapping) {
        return passengerActivityDetailsMapping.stream().map(data -> data.getActivity().getId()).collect(Collectors.toSet());
    }

    private Set<Long> getPassengerIds(List<PassengerDao> passengers) {
        return passengers.stream().map(BaseDao::getId).collect(Collectors.toSet());
    }



    public List<PassengerDao> getAllPassengers(Set<Long> passengerIds) {
        return passengerRepository.findAllById(passengerIds);
    }

    public List<PassengerResponse> buildPassengerResponse(List<Long> passengerIds, List<PassengerDao> passengers) {
        List<PassengerDao> enrolledPassengers = passengers.stream().filter(passenger -> passengerIds.contains(passenger.getId())).toList();
        if (CollectionUtils.isEmpty(enrolledPassengers)) {
            return Collections.emptyList();
        }
        return enrolledPassengers.stream().map(enrolledPassenger -> PassengerResponse.builder()
                .name(enrolledPassenger.getName())
                .mobile(enrolledPassenger.getMobile())
                .build()).toList();
    }

    @Transactional
    public void createPassengerBooking(CreatePassengerBooking request) {
        validatePassengerRequest(request);
        TravelPackageDao travelPackageDao = travelPackageRepository.findById(request.getTravelPackageId())
                .orElseThrow(() -> new RuntimeException("Invalid Travel Package Id"));
        List<PassengerTravelMappingDao> passengerTravelMappingDaos = passengerTravelMappingRepository.findAllByTravelPackageId(request.getTravelPackageId());
        int seatsConsumed = passengerTravelMappingDaos.size();
        if (travelPackageDao.getPassengerCapacity() > seatsConsumed) {
            PassengerDao passengerDao = buildPassengerDao(request);
            PassengerTravelMappingDao passengerTravelMappingDao = buildPassengerTravelMappingDao(travelPackageDao, passengerDao);
            passengerRepository.save(passengerDao);
            passengerTravelMappingRepository.save(passengerTravelMappingDao);
        } else {
            throw new RuntimeException("No seats left");
        }
    }

    private static PassengerTravelMappingDao buildPassengerTravelMappingDao(TravelPackageDao travelPackageDao, PassengerDao passengerDao) {
        return PassengerTravelMappingDao.builder()
                .passenger(passengerDao)
                .travelPackage(travelPackageDao)
                .build();
    }

    private static PassengerDao buildPassengerDao(CreatePassengerBooking request) {
        return PassengerDao.builder()
                .name(request.getName())
                .mobile(request.getMobile())
                .passengerType(request.getPassengerType())
                .totalBalance(request.getTotalBalance())
                .utilizedBalance(0d)
                .remainingBalance(request.getTotalBalance())
                .build();
    }

    private void validatePassengerRequest(CreatePassengerBooking request) {
        if (isEmpty(request.getTravelPackageId())) {
            throw new RuntimeException("Please enter travel package id");
        }
        if (isEmpty(request.getName())) {
            throw new RuntimeException("Name is mandatory");
        }
        if (isEmpty(request.getMobile())) {
            throw new RuntimeException("Mobile is mandatory");
        }
        if (request.getMobile().length() < 10 || request.getMobile().length() > 12) {
            throw new RuntimeException("Enter Valid Mobile number");
        }
        if (isEmpty(request.getPassengerType())) {
            throw new RuntimeException(String.format("Type is mandatory. Please select any one of them %s, %s, %s",
                    PassengerType.GOLD, PassengerType.PREMIUM, PassengerType.STANDARD));
        }
        if (isEmpty(request.getTotalBalance())) {
            throw new RuntimeException("Please enter your balance");
        }
    }
}
