package com.owasp.adservice.repository;

import com.owasp.adservice.entity.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ICarModelRepository extends JpaRepository<CarModel, UUID> {

    CarModel findOneById(UUID id);

    List<CarModel> findAllByDeleted(boolean deleted);

    CarModel findOneByName(String name);
}
