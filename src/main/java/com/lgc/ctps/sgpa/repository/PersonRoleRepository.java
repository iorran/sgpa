package com.lgc.ctps.sgpa.repository;

import com.lgc.ctps.sgpa.domain.PersonRole;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the PersonRole entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PersonRoleRepository extends JpaRepository<PersonRole, Long> {

}
