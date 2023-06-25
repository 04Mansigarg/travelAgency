package com.mansi.travel.repositories;

import com.mansi.travel.models.domains.PassengerActivityDetailsMappingDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PassengerActivityDetailsMappingRepository extends JpaRepository
        <PassengerActivityDetailsMappingDao, Long> {
    List<PassengerActivityDetailsMappingDao> findAllByPassengerIdIn(Set<Long> passengerIds);
}
