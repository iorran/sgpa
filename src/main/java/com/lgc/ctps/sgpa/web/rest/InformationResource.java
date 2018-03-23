package com.lgc.ctps.sgpa.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.lgc.ctps.sgpa.domain.Information;

import com.lgc.ctps.sgpa.repository.InformationRepository;
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
 * REST controller for managing Information.
 */
@RestController
@RequestMapping("/api")
public class InformationResource {

    private final Logger log = LoggerFactory.getLogger(InformationResource.class);

    private static final String ENTITY_NAME = "information";

    private final InformationRepository informationRepository;

    public InformationResource(InformationRepository informationRepository) {
        this.informationRepository = informationRepository;
    }

    /**
     * POST  /information : Create a new information.
     *
     * @param information the information to create
     * @return the ResponseEntity with status 201 (Created) and with body the new information, or with status 400 (Bad Request) if the information has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/information")
    @Timed
    public ResponseEntity<Information> createInformation(@Valid @RequestBody Information information) throws URISyntaxException {
        log.debug("REST request to save Information : {}", information);
        if (information.getId() != null) {
            throw new BadRequestAlertException("A new information cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Information result = informationRepository.save(information);
        return ResponseEntity.created(new URI("/api/information/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /information : Updates an existing information.
     *
     * @param information the information to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated information,
     * or with status 400 (Bad Request) if the information is not valid,
     * or with status 500 (Internal Server Error) if the information couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/information")
    @Timed
    public ResponseEntity<Information> updateInformation(@Valid @RequestBody Information information) throws URISyntaxException {
        log.debug("REST request to update Information : {}", information);
        if (information.getId() == null) {
            return createInformation(information);
        }
        Information result = informationRepository.save(information);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, information.getId().toString()))
            .body(result);
    }

    /**
     * GET  /information : get all the information.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of information in body
     */
    @GetMapping("/information")
    @Timed
    public List<Information> getAllInformation() {
        log.debug("REST request to get all Information");
        return informationRepository.findAll();
        }

    /**
     * GET  /information/:id : get the "id" information.
     *
     * @param id the id of the information to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the information, or with status 404 (Not Found)
     */
    @GetMapping("/information/{id}")
    @Timed
    public ResponseEntity<Information> getInformation(@PathVariable Long id) {
        log.debug("REST request to get Information : {}", id);
        Information information = informationRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(information));
    }

    /**
     * DELETE  /information/:id : delete the "id" information.
     *
     * @param id the id of the information to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/information/{id}")
    @Timed
    public ResponseEntity<Void> deleteInformation(@PathVariable Long id) {
        log.debug("REST request to delete Information : {}", id);
        informationRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
