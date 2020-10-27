package com.kyuan.hotel.service.impl;

import com.kyuan.hotel.service.VipService;
import com.kyuan.hotel.domain.Vip;
import com.kyuan.hotel.repository.VipRepository;
import com.kyuan.hotel.repository.search.VipSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Vip}.
 */
@Service
@Transactional
public class VipServiceImpl implements VipService {

    private final Logger log = LoggerFactory.getLogger(VipServiceImpl.class);

    private final VipRepository vipRepository;

    private final VipSearchRepository vipSearchRepository;

    public VipServiceImpl(VipRepository vipRepository, VipSearchRepository vipSearchRepository) {
        this.vipRepository = vipRepository;
        this.vipSearchRepository = vipSearchRepository;
    }

    @Override
    public Vip save(Vip vip) {
        log.debug("Request to save Vip : {}", vip);
        Vip result = vipRepository.save(vip);
        vipSearchRepository.save(result);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Vip> findAll(Pageable pageable) {
        log.debug("Request to get all Vips");
        return vipRepository.findAll(pageable);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Vip> findOne(Long id) {
        log.debug("Request to get Vip : {}", id);
        return vipRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Vip : {}", id);
        vipRepository.deleteById(id);
        vipSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Vip> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Vips for query {}", query);
        return vipSearchRepository.search(queryStringQuery(query), pageable);    }
}
