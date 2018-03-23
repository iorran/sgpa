package com.lgc.ctps.sgpa.repository;

import com.lgc.ctps.sgpa.domain.InformationValue;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the InformationValue entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InformationValueRepository extends JpaRepository<InformationValue, Long> {

}
