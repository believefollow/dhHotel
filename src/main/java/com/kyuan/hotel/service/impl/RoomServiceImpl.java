package com.kyuan.hotel.service.impl;

import com.kyuan.hotel.service.RoomService;
import com.kyuan.hotel.domain.Room;
import com.kyuan.hotel.repository.RoomRepository;
import com.kyuan.hotel.repository.search.RoomSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Room}.
 */
@Service
@Transactional
public class RoomServiceImpl implements RoomService {

    private final Logger log = LoggerFactory.getLogger(RoomServiceImpl.class);

    private final RoomRepository roomRepository;

    private final RoomSearchRepository roomSearchRepository;

    public RoomServiceImpl(RoomRepository roomRepository, RoomSearchRepository roomSearchRepository) {
        this.roomRepository = roomRepository;
        this.roomSearchRepository = roomSearchRepository;
    }

    @Override
    public Room save(Room room) {
        log.debug("Request to save Room : {}", room);
        Room result = roomRepository.save(room);
        roomSearchRepository.save(result);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Room> findAll(Pageable pageable) {
        log.debug("Request to get all Rooms");
        return roomRepository.findAll(pageable);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Room> findOne(Long id) {
        log.debug("Request to get Room : {}", id);
        return roomRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Room : {}", id);
        roomRepository.deleteById(id);
        roomSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Room> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Rooms for query {}", query);
        return roomSearchRepository.search(queryStringQuery(query), pageable);    }
}
