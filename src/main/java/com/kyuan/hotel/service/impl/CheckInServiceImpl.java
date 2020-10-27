package com.kyuan.hotel.service.impl;

import com.kyuan.hotel.service.CheckInService;
import com.kyuan.hotel.domain.CheckIn;
import com.kyuan.hotel.repository.CheckInRepository;
import com.kyuan.hotel.repository.search.CheckInSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link CheckIn}.
 */
@Service
@Transactional
public class CheckInServiceImpl implements CheckInService {

    private final Logger log = LoggerFactory.getLogger(CheckInServiceImpl.class);

    private final CheckInRepository checkInRepository;

    private final CheckInSearchRepository checkInSearchRepository;

    public CheckInServiceImpl(CheckInRepository checkInRepository, CheckInSearchRepository checkInSearchRepository) {
        this.checkInRepository = checkInRepository;
        this.checkInSearchRepository = checkInSearchRepository;
    }

    @Override
    public CheckIn save(CheckIn checkIn) {
        log.debug("Request to save CheckIn : {}", checkIn);
        CheckIn result = checkInRepository.save(checkIn);
        checkInSearchRepository.save(result);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CheckIn> findAll(Pageable pageable) {
        log.debug("Request to get all CheckIns");
        return checkInRepository.findAll(pageable);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<CheckIn> findOne(Long id) {
        log.debug("Request to get CheckIn : {}", id);
        return checkInRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete CheckIn : {}", id);
        checkInRepository.deleteById(id);
        checkInSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CheckIn> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of CheckIns for query {}", query);
        return checkInSearchRepository.search(queryStringQuery(query), pageable);    }
}
