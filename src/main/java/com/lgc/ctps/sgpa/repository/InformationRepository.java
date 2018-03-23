package com.lgc.ctps.sgpa.repository;

import com.lgc.ctps.sgpa.domain.Information;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Information entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InformationRepository extends JpaRepository<Information, Long> {

}
