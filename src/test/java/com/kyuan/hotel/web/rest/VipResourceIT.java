package com.kyuan.hotel.web.rest;

import com.kyuan.hotel.RedisTestContainerExtension;
import com.kyuan.hotel.DhHotelApp;
import com.kyuan.hotel.domain.Vip;
import com.kyuan.hotel.repository.VipRepository;
import com.kyuan.hotel.repository.search.VipSearchRepository;
import com.kyuan.hotel.service.VipService;

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
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link VipResource} REST controller.
 */
@SpringBootTest(classes = DhHotelApp.class)
@ExtendWith({ RedisTestContainerExtension.class, MockitoExtension.class })
@AutoConfigureMockMvc
@WithMockUser
public class VipResourceIT {

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVED = false;
    private static final Boolean UPDATED_ACTIVED = true;

    @Autowired
    private VipRepository vipRepository;

    @Autowired
    private VipService vipService;

    /**
     * This repository is mocked in the com.kyuan.hotel.repository.search test package.
     *
     * @see com.kyuan.hotel.repository.search.VipSearchRepositoryMockConfiguration
     */
    @Autowired
    private VipSearchRepository mockVipSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVipMockMvc;

    private Vip vip;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vip createEntity(EntityManager em) {
        Vip vip = new Vip()
            .phone(DEFAULT_PHONE)
            .actived(DEFAULT_ACTIVED);
        return vip;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vip createUpdatedEntity(EntityManager em) {
        Vip vip = new Vip()
            .phone(UPDATED_PHONE)
            .actived(UPDATED_ACTIVED);
        return vip;
    }

    @BeforeEach
    public void initTest() {
        vip = createEntity(em);
    }

    @Test
    @Transactional
    public void createVip() throws Exception {
        int databaseSizeBeforeCreate = vipRepository.findAll().size();
        // Create the Vip
        restVipMockMvc.perform(post("/api/vips")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vip)))
            .andExpect(status().isCreated());

        // Validate the Vip in the database
        List<Vip> vipList = vipRepository.findAll();
        assertThat(vipList).hasSize(databaseSizeBeforeCreate + 1);
        Vip testVip = vipList.get(vipList.size() - 1);
        assertThat(testVip.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testVip.isActived()).isEqualTo(DEFAULT_ACTIVED);

        // Validate the Vip in Elasticsearch
        verify(mockVipSearchRepository, times(1)).save(testVip);
    }

    @Test
    @Transactional
    public void createVipWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = vipRepository.findAll().size();

        // Create the Vip with an existing ID
        vip.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restVipMockMvc.perform(post("/api/vips")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vip)))
            .andExpect(status().isBadRequest());

        // Validate the Vip in the database
        List<Vip> vipList = vipRepository.findAll();
        assertThat(vipList).hasSize(databaseSizeBeforeCreate);

        // Validate the Vip in Elasticsearch
        verify(mockVipSearchRepository, times(0)).save(vip);
    }


    @Test
    @Transactional
    public void getAllVips() throws Exception {
        // Initialize the database
        vipRepository.saveAndFlush(vip);

        // Get all the vipList
        restVipMockMvc.perform(get("/api/vips?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vip.getId().intValue())))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].actived").value(hasItem(DEFAULT_ACTIVED.booleanValue())));
    }
    
    @Test
    @Transactional
    public void getVip() throws Exception {
        // Initialize the database
        vipRepository.saveAndFlush(vip);

        // Get the vip
        restVipMockMvc.perform(get("/api/vips/{id}", vip.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(vip.getId().intValue()))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.actived").value(DEFAULT_ACTIVED.booleanValue()));
    }
    @Test
    @Transactional
    public void getNonExistingVip() throws Exception {
        // Get the vip
        restVipMockMvc.perform(get("/api/vips/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateVip() throws Exception {
        // Initialize the database
        vipService.save(vip);

        int databaseSizeBeforeUpdate = vipRepository.findAll().size();

        // Update the vip
        Vip updatedVip = vipRepository.findById(vip.getId()).get();
        // Disconnect from session so that the updates on updatedVip are not directly saved in db
        em.detach(updatedVip);
        updatedVip
            .phone(UPDATED_PHONE)
            .actived(UPDATED_ACTIVED);

        restVipMockMvc.perform(put("/api/vips")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedVip)))
            .andExpect(status().isOk());

        // Validate the Vip in the database
        List<Vip> vipList = vipRepository.findAll();
        assertThat(vipList).hasSize(databaseSizeBeforeUpdate);
        Vip testVip = vipList.get(vipList.size() - 1);
        assertThat(testVip.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testVip.isActived()).isEqualTo(UPDATED_ACTIVED);

        // Validate the Vip in Elasticsearch
        verify(mockVipSearchRepository, times(2)).save(testVip);
    }

    @Test
    @Transactional
    public void updateNonExistingVip() throws Exception {
        int databaseSizeBeforeUpdate = vipRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVipMockMvc.perform(put("/api/vips")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vip)))
            .andExpect(status().isBadRequest());

        // Validate the Vip in the database
        List<Vip> vipList = vipRepository.findAll();
        assertThat(vipList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Vip in Elasticsearch
        verify(mockVipSearchRepository, times(0)).save(vip);
    }

    @Test
    @Transactional
    public void deleteVip() throws Exception {
        // Initialize the database
        vipService.save(vip);

        int databaseSizeBeforeDelete = vipRepository.findAll().size();

        // Delete the vip
        restVipMockMvc.perform(delete("/api/vips/{id}", vip.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Vip> vipList = vipRepository.findAll();
        assertThat(vipList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Vip in Elasticsearch
        verify(mockVipSearchRepository, times(1)).deleteById(vip.getId());
    }

    @Test
    @Transactional
    public void searchVip() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        vipService.save(vip);
        when(mockVipSearchRepository.search(queryStringQuery("id:" + vip.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(vip), PageRequest.of(0, 1), 1));

        // Search the vip
        restVipMockMvc.perform(get("/api/_search/vips?query=id:" + vip.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vip.getId().intValue())))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].actived").value(hasItem(DEFAULT_ACTIVED.booleanValue())));
    }
}
