package com.lgc.ctps.sgpa.repository;

import com.lgc.ctps.sgpa.domain.UseCase;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the UseCase entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UseCaseRepository extends JpaRepository<UseCase, Long> {

}
