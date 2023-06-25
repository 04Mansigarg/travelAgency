package com.mansi.travel.repositories;

import com.mansi.travel.models.domains.TravelDestinationMappingDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TravelDestinationMappingRepository extends JpaRepository<TravelDestinationMappingDao, Long> {
    List<TravelDestinationMappingDao> findAllByTravelPackageIdIn(List<Long> travelPackageIds);
}
