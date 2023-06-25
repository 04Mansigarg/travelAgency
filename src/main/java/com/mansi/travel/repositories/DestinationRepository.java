package com.mansi.travel.repositories;

import com.mansi.travel.models.domains.DestinationDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DestinationRepository extends JpaRepository<DestinationDao, Long> {
}
