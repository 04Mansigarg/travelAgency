package com.mansi.travel.mapper;

import com.mansi.travel.models.domains.ActivityDao;
import com.mansi.travel.models.dto.response.ActivityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TravelMapper {

    public ActivityResponse buildActivityResponse(ActivityDao activity) {
        return ActivityResponse.builder()
                .name(activity.getName())
                .description(activity.getDescription())
                .cost(activity.getCost())
                .capacity(activity.getCapacity())
                .availableSlots(activity.getCapacity() - activity.getConsumption())
                .build();
    }
}
