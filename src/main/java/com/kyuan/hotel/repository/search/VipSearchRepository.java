package com.kyuan.hotel.repository.search;

import com.kyuan.hotel.domain.Vip;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link Vip} entity.
 */
public interface VipSearchRepository extends ElasticsearchRepository<Vip, Long> {
}
