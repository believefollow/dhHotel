package com.kyuan.hotel.repository.search;

import com.kyuan.hotel.domain.Room;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link Room} entity.
 */
public interface RoomSearchRepository extends ElasticsearchRepository<Room, Long> {
}
