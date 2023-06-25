package com.mansi.travel.repositories;

import com.mansi.travel.models.domains.TravelPackageDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelPackageRepository extends JpaRepository<TravelPackageDao, Long> {
}
