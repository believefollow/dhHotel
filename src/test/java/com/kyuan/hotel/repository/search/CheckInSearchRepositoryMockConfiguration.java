package com.kyuan.hotel.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link CheckInSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class CheckInSearchRepositoryMockConfiguration {

    @MockBean
    private CheckInSearchRepository mockCheckInSearchRepository;

}
