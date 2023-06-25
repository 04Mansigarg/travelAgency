package com.mansi.travel.service;

import com.mansi.travel.mapper.TravelMapper;
import com.mansi.travel.models.domains.ActivityDao;
import com.mansi.travel.models.domains.DestinationDao;
import com.mansi.travel.models.domains.TravelDestinationMappingDao;
import com.mansi.travel.models.domains.TravelPackageDao;
import com.mansi.travel.models.dto.request.DestinationRequest;
import com.mansi.travel.models.dto.response.ActivityResponse;
import com.mansi.travel.models.dto.response.DestinationResponse;
import com.mansi.travel.repositories.DestinationRepository;
import com.mansi.travel.repositories.TravelDestinationMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Component
@Slf4j
@RequiredArgsConstructor
public class DestinationService {

    private final ActivityService activityService;
    private final DestinationRepository destinationRepository;
    private final TravelDestinationMappingRepository travelDestinationMappingRepository;
    private final TravelMapper travelMapper;

    public List<DestinationResponse> buildDestinationDetails(List<Long> destinationIds) {
        if (CollectionUtils.isEmpty(destinationIds)) {
            return Collections.emptyList();
        }
        List<DestinationDao> destinations = getDestinations(destinationIds);
        List<DestinationResponse> destinationResponses = new ArrayList<>();
        List<ActivityDao> activities = activityService.getAllActivities(destinationIds);
        Map<Long, List<ActivityDao>> mapOfDestinationToActivityMapping = activities.stream().collect(Collectors.groupingBy(
                activity -> activity.getDestination().getId()
        ));
        destinations.forEach(destination -> destinationResponses.add(DestinationResponse.builder()
                .name(destination.getName())
                .activities(getActivities(mapOfDestinationToActivityMapping.get(destination.getId())))
                .build()));
        return destinationResponses;
    }

    public List<DestinationDao> buildAndSaveDestinations(List<DestinationRequest> destinationRequests, TravelPackageDao travelPackage) {
        if (CollectionUtils.isEmpty(destinationRequests)) {
            return Collections.emptyList();
        }
        List<DestinationDao> destinationDaos = new ArrayList<>();
        List<TravelDestinationMappingDao> travelDestinationMappings = new ArrayList<>();
        destinationRequests.forEach(destinationRequest -> {
            DestinationDao destination = buildDestinationDao(destinationRequest);
            List<ActivityDao> activities = activityService.buildAndSaveActivities(destinationRequest.getActivities(), destination);
            destination.setActivities(new HashSet<>(activities));
            TravelDestinationMappingDao travelDestinationMapping = buildTravelDestinationMappingDao(travelPackage, destination);
            destinationDaos.add(destination);
            travelDestinationMappings.add(travelDestinationMapping);
        });
        travelPackage.setTravelDestinationMappings(new HashSet<>(travelDestinationMappings));
        List<DestinationDao> destinationList = destinationRepository.saveAll(destinationDaos);
        travelDestinationMappingRepository.saveAll(travelDestinationMappings);
        return destinationList;
    }

    private TravelDestinationMappingDao buildTravelDestinationMappingDao(TravelPackageDao travelPackage, DestinationDao destination) {
        return TravelDestinationMappingDao.builder()
                .destination(destination)
                .travelPackage(travelPackage).build();
    }

    private DestinationDao buildDestinationDao(DestinationRequest destinationRequest) {
        return DestinationDao.builder()
                .name(destinationRequest.getName())
                .build();
    }

    private List<ActivityResponse> getActivities(List<ActivityDao> activities) {
        if (CollectionUtils.isEmpty(activities)) {
            return Collections.emptyList();
        }
        List<ActivityResponse> activitiesResponses = new ArrayList<>();
        activities.forEach(activity -> activitiesResponses.add(travelMapper.buildActivityResponse(activity)));
        return activitiesResponses;
    }

    public List<DestinationDao> getDestinations(List<Long> destinationIds) {
        return destinationRepository.findAllById(destinationIds);
    }
}
