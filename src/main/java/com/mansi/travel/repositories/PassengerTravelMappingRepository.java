package com.mansi.travel.repositories;

import com.mansi.travel.models.domains.PassengerTravelMappingDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PassengerTravelMappingRepository extends JpaRepository<PassengerTravelMappingDao, Long> {
    List<PassengerTravelMappingDao> findAllByTravelPackageIdIn(List<Long> travelPackageIds);

    List<PassengerTravelMappingDao> findAllByTravelPackageId(Long travelPackageId);
}
