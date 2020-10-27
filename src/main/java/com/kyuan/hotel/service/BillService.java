package com.kyuan.hotel.service;

import com.kyuan.hotel.domain.Bill;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link Bill}.
 */
public interface BillService {

    /**
     * Save a bill.
     *
     * @param bill the entity to save.
     * @return the persisted entity.
     */
    Bill save(Bill bill);

    /**
     * Get all the bills.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Bill> findAll(Pageable pageable);


    /**
     * Get the "id" bill.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Bill> findOne(Long id);

    /**
     * Delete the "id" bill.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the bill corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Bill> search(String query, Pageable pageable);
}
