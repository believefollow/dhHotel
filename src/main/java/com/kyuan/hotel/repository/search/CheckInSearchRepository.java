package com.kyuan.hotel.repository.search;

import com.kyuan.hotel.domain.CheckIn;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link CheckIn} entity.
 */
public interface CheckInSearchRepository extends ElasticsearchRepository<CheckIn, Long> {
}
