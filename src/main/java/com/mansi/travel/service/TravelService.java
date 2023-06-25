package com.mansi.travel.service;

import com.mansi.travel.models.domains.BaseDao;
import com.mansi.travel.models.domains.DestinationDao;
import com.mansi.travel.models.domains.PassengerDao;
import com.mansi.travel.models.domains.PassengerTravelMappingDao;
import com.mansi.travel.models.domains.TravelDestinationMappingDao;
import com.mansi.travel.models.domains.TravelPackageDao;
import com.mansi.travel.models.dto.request.CreateTravelPackageRequest;
import com.mansi.travel.models.dto.response.TravelPackageDetails;
import com.mansi.travel.repositories.PassengerTravelMappingRepository;
import com.mansi.travel.repositories.TravelDestinationMappingRepository;
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
import java.util.stream.Collectors;

@Service
@Component
@Slf4j
@RequiredArgsConstructor
public class TravelService {

    private final TravelPackageRepository travelPackageRepository;
    private final TravelDestinationMappingRepository travelDestinationMappingRepository;
    private final DestinationService destinationService;
    private final PassengerService passengerService;

    private final PassengerTravelMappingRepository passengerTravelMappingRepository;

//    1. Print itinerary of the travel package including:
//            a. travel package name,
//            b. destinations and details of the activities available at each destination, like name, cost, capacity and description.

    public List<TravelPackageDetails> getTravelPackageDetails() {
        List<TravelPackageDao> travelPackages = travelPackageRepository.findAll();
        Map<Long, List<Long>> travelPackageIdToDestinationIds = getMapOfTravelIdAndDestinationIds(getTravelDestinationMappings(travelPackages));
        return buildTravelPackageDetails(travelPackages, travelPackageIdToDestinationIds);
    }

//    2. Print the passenger list of the travel package including:
//         a. package name,
//         b. passenger capacity,
//         c. number of passengers currently enrolled and
//         d. name and number of each passenger

    public List<TravelPackageDetails> getTravelPackagePassengerDetails() {
        List<TravelPackageDao> travelPackages = travelPackageRepository.findAll();
        List<PassengerTravelMappingDao> passengerTravelMappings = getPassengerTravelMapping(travelPackages);
        List<PassengerDao> passengers = passengerService.getAllPassengers(getPassengerIds(passengerTravelMappings));
        Map<Long, List<Long>> travelPackageIdToPassengersIdMap = getMapOfTravelIdAndPassengerIds(passengerTravelMappings);
        return buildTravelPackagePassengersDetails(travelPackages, passengers, travelPackageIdToPassengersIdMap);
    }

    @Transactional
    public void createTravelPackage(CreateTravelPackageRequest request) {
        TravelPackageDao travelPackage = travelPackageRepository.save(buildTravelPackage(request));
        destinationService.buildAndSaveDestinations(request.getDestinations(), travelPackage);
    }

    private TravelPackageDao buildTravelPackage(CreateTravelPackageRequest request) {
        return TravelPackageDao.builder()
                .name(request.getPackageName())
                .passengerCapacity(request.getPackageCapacity())
                .build();
    }


    private List<TravelPackageDetails> buildTravelPackagePassengersDetails(List<TravelPackageDao> travelPackages,
                                                                 List<PassengerDao> passengers,
                                                                 Map<Long, List<Long>> travelPackageIdToPassengersIdMap) {
        if (CollectionUtils.isEmpty(travelPackageIdToPassengersIdMap)) {
            return Collections.emptyList();
        }
        List<TravelPackageDetails> travelPackageDetails = new ArrayList<>();
        travelPackageIdToPassengersIdMap.forEach((travelPackageId, passengerIds) -> {
            TravelPackageDao travelPackage = getTravelPackage(travelPackages, travelPackageId);
            travelPackageDetails.add(TravelPackageDetails.builder()
                    .packageName(travelPackage.getName())
                    .packageCapacity(travelPackage.getPassengerCapacity())
                    .passengerEnrolled((long) passengerIds.size())
                    .passengers(passengerService.buildPassengerResponse(passengerIds, passengers))
                    .build());
        });
        return travelPackageDetails;
    }

    private Set<Long> getPassengerIds(List<PassengerTravelMappingDao> passengerTravelMappings) {
        return passengerTravelMappings.stream()
                .map(passengerTravelMapping -> passengerTravelMapping.getPassenger().getId()).collect(Collectors.toSet());
    }

    private List<TravelPackageDetails> buildTravelPackageDetails(List<TravelPackageDao> travelPackages,
                                                                 Map<Long, List<Long>> travelPackageIdToDestinationIds) {
        if (CollectionUtils.isEmpty(travelPackageIdToDestinationIds)) {
            return Collections.emptyList();
        }
        List<TravelPackageDetails> travelPackageDetails = new ArrayList<>();
        travelPackageIdToDestinationIds.forEach((travelPackageId, destinationIds) -> {
            TravelPackageDao travelPackage = getTravelPackage(travelPackages, travelPackageId);
            travelPackageDetails.add(TravelPackageDetails.builder()
                    .packageName(travelPackage.getName())
                    .packageCapacity(travelPackage.getPassengerCapacity())
                    .destinations(destinationService.buildDestinationDetails(destinationIds))
                    .build());
        });
        return travelPackageDetails;
    }

    private TravelPackageDao getTravelPackage(List<TravelPackageDao> travelPackages, Long travelPackageId) {
        return travelPackages.stream()
                .filter(travel -> travelPackageId.equals(travel.getId())).findFirst().get();
    }

    private List<TravelDestinationMappingDao> getTravelDestinationMappings(List<TravelPackageDao> travelPackages) {
        return travelDestinationMappingRepository.findAllByTravelPackageIdIn(getTravelPackageId(travelPackages));
    }

    private List<PassengerTravelMappingDao> getPassengerTravelMapping(List<TravelPackageDao> travelPackages) {
        return passengerTravelMappingRepository.findAllByTravelPackageIdIn(getTravelPackageId(travelPackages));
    }

    private  List<Long> getTravelPackageId(List<TravelPackageDao> travelPackages) {
        return travelPackages.stream().map(BaseDao::getId).toList();
    }

    private Map<Long, List<Long>> getMapOfTravelIdAndDestinationIds(List<TravelDestinationMappingDao> travelDestinationMappings) {
        if (CollectionUtils.isEmpty(travelDestinationMappings)) {
            return Collections.emptyMap();
        }
        return travelDestinationMappings.stream().collect(Collectors.groupingBy(
                        travelDestinationMapping -> travelDestinationMapping.getTravelPackage().getId(),
                        Collectors.mapping(travelDestinationMapping -> travelDestinationMapping.getDestination().getId(), Collectors.toList())));
    }

    private Map<Long, List<Long>> getMapOfTravelIdAndPassengerIds(List<PassengerTravelMappingDao> passengerTravelMappings) {
        if (CollectionUtils.isEmpty(passengerTravelMappings)) {
            return Collections.emptyMap();
        }
        return passengerTravelMappings.stream().collect(Collectors.groupingBy(
                passengerTravelMapping -> passengerTravelMapping.getTravelPackage().getId(),
                Collectors.mapping(travelDestinationMapping -> travelDestinationMapping.getPassenger().getId(), Collectors.toList())));
    }
}
