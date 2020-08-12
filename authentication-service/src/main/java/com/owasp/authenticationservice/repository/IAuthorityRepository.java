package com.owasp.authenticationservice.repository;

import com.owasp.authenticationservice.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAuthorityRepository extends JpaRepository<Authority, Long> {

    Authority findByName(String name);

    Authority findOneByName(String name);

    Authority findOneById(Long id);
}
