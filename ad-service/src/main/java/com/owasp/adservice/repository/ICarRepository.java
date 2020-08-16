package com.owasp.adservice.repository;

import com.owasp.adservice.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ICarRepository extends JpaRepository<Car, UUID> {

    Car findOneById(UUID id);

    List<Car> findAllByDeleted(boolean deleted);
}
