package com.lgc.ctps.sgpa.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.lgc.ctps.sgpa.domain.Permission;

import com.lgc.ctps.sgpa.repository.PermissionRepository;
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
 * REST controller for managing Permission.
 */
@RestController
@RequestMapping("/api")
public class PermissionResource {

    private final Logger log = LoggerFactory.getLogger(PermissionResource.class);

    private static final String ENTITY_NAME = "permission";

    private final PermissionRepository permissionRepository;

    public PermissionResource(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    /**
     * POST  /permissions : Create a new permission.
     *
     * @param permission the permission to create
     * @return the ResponseEntity with status 201 (Created) and with body the new permission, or with status 400 (Bad Request) if the permission has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/permissions")
    @Timed
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permission) throws URISyntaxException {
        log.debug("REST request to save Permission : {}", permission);
        if (permission.getId() != null) {
            throw new BadRequestAlertException("A new permission cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Permission result = permissionRepository.save(permission);
        return ResponseEntity.created(new URI("/api/permissions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /permissions : Updates an existing permission.
     *
     * @param permission the permission to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated permission,
     * or with status 400 (Bad Request) if the permission is not valid,
     * or with status 500 (Internal Server Error) if the permission couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/permissions")
    @Timed
    public ResponseEntity<Permission> updatePermission(@Valid @RequestBody Permission permission) throws URISyntaxException {
        log.debug("REST request to update Permission : {}", permission);
        if (permission.getId() == null) {
            return createPermission(permission);
        }
        Permission result = permissionRepository.save(permission);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, permission.getId().toString()))
            .body(result);
    }

    /**
     * GET  /permissions : get all the permissions.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of permissions in body
     */
    @GetMapping("/permissions")
    @Timed
    public List<Permission> getAllPermissions() {
        log.debug("REST request to get all Permissions");
        return permissionRepository.findAll();
        }

    /**
     * GET  /permissions/:id : get the "id" permission.
     *
     * @param id the id of the permission to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the permission, or with status 404 (Not Found)
     */
    @GetMapping("/permissions/{id}")
    @Timed
    public ResponseEntity<Permission> getPermission(@PathVariable Long id) {
        log.debug("REST request to get Permission : {}", id);
        Permission permission = permissionRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(permission));
    }

    /**
     * DELETE  /permissions/:id : delete the "id" permission.
     *
     * @param id the id of the permission to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/permissions/{id}")
    @Timed
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        log.debug("REST request to delete Permission : {}", id);
        permissionRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
