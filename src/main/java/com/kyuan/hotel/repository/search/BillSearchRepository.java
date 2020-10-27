package com.kyuan.hotel.repository.search;

import com.kyuan.hotel.domain.Bill;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link Bill} entity.
 */
public interface BillSearchRepository extends ElasticsearchRepository<Bill, Long> {
}
