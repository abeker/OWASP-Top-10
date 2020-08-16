package com.owasp.adservice.repository;

import com.owasp.adservice.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IRatingRepository extends JpaRepository<Rating, UUID> {

    Rating findOneBySimpleUserAndAd_Id(UUID customerId, UUID adId);

    List<Rating> findAllBySimpleUser(UUID id);

    List<Rating> findAllByAd_Id(UUID id);
}
