package com.lgc.ctps.sgpa.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.lgc.ctps.sgpa.domain.PersonRole;

import com.lgc.ctps.sgpa.repository.PersonRoleRepository;
import com.lgc.ctps.sgpa.web.rest.errors.BadRequestAlertException;
import com.lgc.ctps.sgpa.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing PersonRole.
 */
@RestController
@RequestMapping("/api")
public class PersonRoleResource {

    private final Logger log = LoggerFactory.getLogger(PersonRoleResource.class);

    private static final String ENTITY_NAME = "personRole";

    private final PersonRoleRepository personRoleRepository;

    public PersonRoleResource(PersonRoleRepository personRoleRepository) {
        this.personRoleRepository = personRoleRepository;
    }

    /**
     * POST  /person-roles : Create a new personRole.
     *
     * @param personRole the personRole to create
     * @return the ResponseEntity with status 201 (Created) and with body the new personRole, or with status 400 (Bad Request) if the personRole has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/person-roles")
    @Timed
    public ResponseEntity<PersonRole> createPersonRole(@Valid @RequestBody PersonRole personRole) throws URISyntaxException {
        log.debug("REST request to save PersonRole : {}", personRole);
        if (personRole.getId() != null) {
            throw new BadRequestAlertException("A new personRole cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PersonRole result = personRoleRepository.save(personRole);
        return ResponseEntity.created(new URI("/api/person-roles/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /person-roles : Updates an existing personRole.
     *
     * @param personRole the personRole to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated personRole,
     * or with status 400 (Bad Request) if the personRole is not valid,
     * or with status 500 (Internal Server Error) if the personRole couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/person-roles")
    @Timed
    public ResponseEntity<PersonRole> updatePersonRole(@Valid @RequestBody PersonRole personRole) throws URISyntaxException {
        log.debug("REST request to update PersonRole : {}", personRole);
        if (personRole.getId() == null) {
            return createPersonRole(personRole);
        }
        PersonRole result = personRoleRepository.save(personRole);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, personRole.getId().toString()))
            .body(result);
    }

    /**
     * GET  /person-roles : get all the personRoles.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of personRoles in body
     */
    @GetMapping("/person-roles")
    @Timed
    public List<PersonRole> getAllPersonRoles() {
        log.debug("REST request to get all PersonRoles");
        return personRoleRepository.findAll();
        }

    /**
     * GET  /person-roles/:id : get the "id" personRole.
     *
     * @param id the id of the personRole to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the personRole, or with status 404 (Not Found)
     */
    @GetMapping("/person-roles/{id}")
    @Timed
    public ResponseEntity<PersonRole> getPersonRole(@PathVariable Long id) {
        log.debug("REST request to get PersonRole : {}", id);
        PersonRole personRole = personRoleRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(personRole));
    }

    /**
     * DELETE  /person-roles/:id : delete the "id" personRole.
     *
     * @param id the id of the personRole to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/person-roles/{id}")
    @Timed
    public ResponseEntity<Void> deletePersonRole(@PathVariable Long id) {
        log.debug("REST request to delete PersonRole : {}", id);
        personRoleRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
