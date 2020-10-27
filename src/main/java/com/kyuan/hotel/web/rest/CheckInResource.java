package com.kyuan.hotel.web.rest;

import com.kyuan.hotel.domain.CheckIn;
import com.kyuan.hotel.service.CheckInService;
import com.kyuan.hotel.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link com.kyuan.hotel.domain.CheckIn}.
 */
@RestController
@RequestMapping("/api")
public class CheckInResource {

    private final Logger log = LoggerFactory.getLogger(CheckInResource.class);

    private static final String ENTITY_NAME = "checkIn";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CheckInService checkInService;

    public CheckInResource(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    /**
     * {@code POST  /check-ins} : Create a new checkIn.
     *
     * @param checkIn the checkIn to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new checkIn, or with status {@code 400 (Bad Request)} if the checkIn has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/check-ins")
    public ResponseEntity<CheckIn> createCheckIn(@RequestBody CheckIn checkIn) throws URISyntaxException {
        log.debug("REST request to save CheckIn : {}", checkIn);
        if (checkIn.getId() != null) {
            throw new BadRequestAlertException("A new checkIn cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CheckIn result = checkInService.save(checkIn);
        return ResponseEntity.created(new URI("/api/check-ins/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /check-ins} : Updates an existing checkIn.
     *
     * @param checkIn the checkIn to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated checkIn,
     * or with status {@code 400 (Bad Request)} if the checkIn is not valid,
     * or with status {@code 500 (Internal Server Error)} if the checkIn couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/check-ins")
    public ResponseEntity<CheckIn> updateCheckIn(@RequestBody CheckIn checkIn) throws URISyntaxException {
        log.debug("REST request to update CheckIn : {}", checkIn);
        if (checkIn.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        CheckIn result = checkInService.save(checkIn);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, checkIn.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /check-ins} : get all the checkIns.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of checkIns in body.
     */
    @GetMapping("/check-ins")
    public ResponseEntity<List<CheckIn>> getAllCheckIns(Pageable pageable) {
        log.debug("REST request to get a page of CheckIns");
        Page<CheckIn> page = checkInService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /check-ins/:id} : get the "id" checkIn.
     *
     * @param id the id of the checkIn to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the checkIn, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/check-ins/{id}")
    public ResponseEntity<CheckIn> getCheckIn(@PathVariable Long id) {
        log.debug("REST request to get CheckIn : {}", id);
        Optional<CheckIn> checkIn = checkInService.findOne(id);
        return ResponseUtil.wrapOrNotFound(checkIn);
    }

    /**
     * {@code DELETE  /check-ins/:id} : delete the "id" checkIn.
     *
     * @param id the id of the checkIn to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/check-ins/{id}")
    public ResponseEntity<Void> deleteCheckIn(@PathVariable Long id) {
        log.debug("REST request to delete CheckIn : {}", id);
        checkInService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/check-ins?query=:query} : search for the checkIn corresponding
     * to the query.
     *
     * @param query the query of the checkIn search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/check-ins")
    public ResponseEntity<List<CheckIn>> searchCheckIns(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of CheckIns for query {}", query);
        Page<CheckIn> page = checkInService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
        }
}
