package com.kyuan.hotel.web.rest;

import com.kyuan.hotel.RedisTestContainerExtension;
import com.kyuan.hotel.DhHotelApp;
import com.kyuan.hotel.domain.CheckIn;
import com.kyuan.hotel.repository.CheckInRepository;
import com.kyuan.hotel.repository.search.CheckInSearchRepository;
import com.kyuan.hotel.service.CheckInService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link CheckInResource} REST controller.
 */
@SpringBootTest(classes = DhHotelApp.class)
@ExtendWith({ RedisTestContainerExtension.class, MockitoExtension.class })
@AutoConfigureMockMvc
@WithMockUser
public class CheckInResourceIT {

    private static final LocalDate DEFAULT_START_TIME = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_TIME = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_TIME = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_TIME = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private CheckInRepository checkInRepository;

    @Autowired
    private CheckInService checkInService;

    /**
     * This repository is mocked in the com.kyuan.hotel.repository.search test package.
     *
     * @see com.kyuan.hotel.repository.search.CheckInSearchRepositoryMockConfiguration
     */
    @Autowired
    private CheckInSearchRepository mockCheckInSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCheckInMockMvc;

    private CheckIn checkIn;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CheckIn createEntity(EntityManager em) {
        CheckIn checkIn = new CheckIn()
            .startTime(DEFAULT_START_TIME)
            .endTime(DEFAULT_END_TIME);
        return checkIn;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CheckIn createUpdatedEntity(EntityManager em) {
        CheckIn checkIn = new CheckIn()
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME);
        return checkIn;
    }

    @BeforeEach
    public void initTest() {
        checkIn = createEntity(em);
    }

    @Test
    @Transactional
    public void createCheckIn() throws Exception {
        int databaseSizeBeforeCreate = checkInRepository.findAll().size();
        // Create the CheckIn
        restCheckInMockMvc.perform(post("/api/check-ins")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(checkIn)))
            .andExpect(status().isCreated());

        // Validate the CheckIn in the database
        List<CheckIn> checkInList = checkInRepository.findAll();
        assertThat(checkInList).hasSize(databaseSizeBeforeCreate + 1);
        CheckIn testCheckIn = checkInList.get(checkInList.size() - 1);
        assertThat(testCheckIn.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testCheckIn.getEndTime()).isEqualTo(DEFAULT_END_TIME);

        // Validate the CheckIn in Elasticsearch
        verify(mockCheckInSearchRepository, times(1)).save(testCheckIn);
    }

    @Test
    @Transactional
    public void createCheckInWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = checkInRepository.findAll().size();

        // Create the CheckIn with an existing ID
        checkIn.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCheckInMockMvc.perform(post("/api/check-ins")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(checkIn)))
            .andExpect(status().isBadRequest());

        // Validate the CheckIn in the database
        List<CheckIn> checkInList = checkInRepository.findAll();
        assertThat(checkInList).hasSize(databaseSizeBeforeCreate);

        // Validate the CheckIn in Elasticsearch
        verify(mockCheckInSearchRepository, times(0)).save(checkIn);
    }


    @Test
    @Transactional
    public void getAllCheckIns() throws Exception {
        // Initialize the database
        checkInRepository.saveAndFlush(checkIn);

        // Get all the checkInList
        restCheckInMockMvc.perform(get("/api/check-ins?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(checkIn.getId().intValue())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())));
    }
    
    @Test
    @Transactional
    public void getCheckIn() throws Exception {
        // Initialize the database
        checkInRepository.saveAndFlush(checkIn);

        // Get the checkIn
        restCheckInMockMvc.perform(get("/api/check-ins/{id}", checkIn.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(checkIn.getId().intValue()))
            .andExpect(jsonPath("$.startTime").value(DEFAULT_START_TIME.toString()))
            .andExpect(jsonPath("$.endTime").value(DEFAULT_END_TIME.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingCheckIn() throws Exception {
        // Get the checkIn
        restCheckInMockMvc.perform(get("/api/check-ins/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCheckIn() throws Exception {
        // Initialize the database
        checkInService.save(checkIn);

        int databaseSizeBeforeUpdate = checkInRepository.findAll().size();

        // Update the checkIn
        CheckIn updatedCheckIn = checkInRepository.findById(checkIn.getId()).get();
        // Disconnect from session so that the updates on updatedCheckIn are not directly saved in db
        em.detach(updatedCheckIn);
        updatedCheckIn
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME);

        restCheckInMockMvc.perform(put("/api/check-ins")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedCheckIn)))
            .andExpect(status().isOk());

        // Validate the CheckIn in the database
        List<CheckIn> checkInList = checkInRepository.findAll();
        assertThat(checkInList).hasSize(databaseSizeBeforeUpdate);
        CheckIn testCheckIn = checkInList.get(checkInList.size() - 1);
        assertThat(testCheckIn.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testCheckIn.getEndTime()).isEqualTo(UPDATED_END_TIME);

        // Validate the CheckIn in Elasticsearch
        verify(mockCheckInSearchRepository, times(2)).save(testCheckIn);
    }

    @Test
    @Transactional
    public void updateNonExistingCheckIn() throws Exception {
        int databaseSizeBeforeUpdate = checkInRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCheckInMockMvc.perform(put("/api/check-ins")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(checkIn)))
            .andExpect(status().isBadRequest());

        // Validate the CheckIn in the database
        List<CheckIn> checkInList = checkInRepository.findAll();
        assertThat(checkInList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CheckIn in Elasticsearch
        verify(mockCheckInSearchRepository, times(0)).save(checkIn);
    }

    @Test
    @Transactional
    public void deleteCheckIn() throws Exception {
        // Initialize the database
        checkInService.save(checkIn);

        int databaseSizeBeforeDelete = checkInRepository.findAll().size();

        // Delete the checkIn
        restCheckInMockMvc.perform(delete("/api/check-ins/{id}", checkIn.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CheckIn> checkInList = checkInRepository.findAll();
        assertThat(checkInList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the CheckIn in Elasticsearch
        verify(mockCheckInSearchRepository, times(1)).deleteById(checkIn.getId());
    }

    @Test
    @Transactional
    public void searchCheckIn() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        checkInService.save(checkIn);
        when(mockCheckInSearchRepository.search(queryStringQuery("id:" + checkIn.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(checkIn), PageRequest.of(0, 1), 1));

        // Search the checkIn
        restCheckInMockMvc.perform(get("/api/_search/check-ins?query=id:" + checkIn.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(checkIn.getId().intValue())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())));
    }
}
