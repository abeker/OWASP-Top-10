package com.owasp.adservice.repository;

import com.owasp.adservice.entity.Ad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IAdRepository extends JpaRepository<Ad, UUID> {

    Ad findOneById(UUID id);

    List<Ad> findAllByAgent(UUID agent_id);

}