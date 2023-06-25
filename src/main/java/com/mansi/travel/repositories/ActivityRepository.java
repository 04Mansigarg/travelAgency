package com.mansi.travel.repositories;

import com.mansi.travel.models.domains.ActivityDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<ActivityDao, Long> {
    List<ActivityDao> findAllByDestinationIdIn(List<Long> destinationIds);

    @Query(value = "select a.* from activities a where a.capacity - a.consumption > 0", nativeQuery = true)
    List<ActivityDao> findAllAvailableActivities();
}
