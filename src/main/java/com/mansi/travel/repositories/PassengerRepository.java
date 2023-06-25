package com.mansi.travel.repositories;

import com.mansi.travel.models.domains.PassengerDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassengerRepository extends JpaRepository<PassengerDao, Long> {
}
