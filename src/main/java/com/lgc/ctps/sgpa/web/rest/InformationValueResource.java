package com.lgc.ctps.sgpa.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.lgc.ctps.sgpa.domain.InformationValue;

import com.lgc.ctps.sgpa.repository.InformationValueRepository;
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
 * REST controller for managing InformationValue.
 */
@RestController
@RequestMapping("/api")
public class InformationValueResource {

    private final Logger log = LoggerFactory.getLogger(InformationValueResource.class);

    private static final String ENTITY_NAME = "informationValue";

    private final InformationValueRepository informationValueRepository;

    public InformationValueResource(InformationValueRepository informationValueRepository) {
        this.informationValueRepository = informationValueRepository;
    }

    /**
     * POST  /information-values : Create a new informationValue.
     *
     * @param informationValue the informationValue to create
     * @return the ResponseEntity with status 201 (Created) and with body the new informationValue, or with status 400 (Bad Request) if the informationValue has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/information-values")
    @Timed
    public ResponseEntity<InformationValue> createInformationValue(@Valid @RequestBody InformationValue informationValue) throws URISyntaxException {
        log.debug("REST request to save InformationValue : {}", informationValue);
        if (informationValue.getId() != null) {
            throw new BadRequestAlertException("A new informationValue cannot already have an ID", ENTITY_NAME, "idexists");
        }
        InformationValue result = informationValueRepository.save(informationValue);
        return ResponseEntity.created(new URI("/api/information-values/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /information-values : Updates an existing informationValue.
     *
     * @param informationValue the informationValue to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated informationValue,
     * or with status 400 (Bad Request) if the informationValue is not valid,
     * or with status 500 (Internal Server Error) if the informationValue couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/information-values")
    @Timed
    public ResponseEntity<InformationValue> updateInformationValue(@Valid @RequestBody InformationValue informationValue) throws URISyntaxException {
        log.debug("REST request to update InformationValue : {}", informationValue);
        if (informationValue.getId() == null) {
            return createInformationValue(informationValue);
        }
        InformationValue result = informationValueRepository.save(informationValue);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, informationValue.getId().toString()))
            .body(result);
    }

    /**
     * GET  /information-values : get all the informationValues.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of informationValues in body
     */
    @GetMapping("/information-values")
    @Timed
    public List<InformationValue> getAllInformationValues() {
        log.debug("REST request to get all InformationValues");
        return informationValueRepository.findAll();
        }

    /**
     * GET  /information-values/:id : get the "id" informationValue.
     *
     * @param id the id of the informationValue to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the informationValue, or with status 404 (Not Found)
     */
    @GetMapping("/information-values/{id}")
    @Timed
    public ResponseEntity<InformationValue> getInformationValue(@PathVariable Long id) {
        log.debug("REST request to get InformationValue : {}", id);
        InformationValue informationValue = informationValueRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(informationValue));
    }

    /**
     * DELETE  /information-values/:id : delete the "id" informationValue.
     *
     * @param id the id of the informationValue to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/information-values/{id}")
    @Timed
    public ResponseEntity<Void> deleteInformationValue(@PathVariable Long id) {
        log.debug("REST request to delete InformationValue : {}", id);
        informationValueRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
