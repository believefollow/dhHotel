package com.kyuan.hotel.web.rest;

import com.kyuan.hotel.RedisTestContainerExtension;
import com.kyuan.hotel.DhHotelApp;
import com.kyuan.hotel.domain.Room;
import com.kyuan.hotel.repository.RoomRepository;
import com.kyuan.hotel.repository.search.RoomSearchRepository;
import com.kyuan.hotel.service.RoomService;

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

import com.kyuan.hotel.domain.enumeration.RoomType;
import com.kyuan.hotel.domain.enumeration.PriceType;
/**
 * Integration tests for the {@link RoomResource} REST controller.
 */
@SpringBootTest(classes = DhHotelApp.class)
@ExtendWith({ RedisTestContainerExtension.class, MockitoExtension.class })
@AutoConfigureMockMvc
@WithMockUser
public class RoomResourceIT {

    private static final RoomType DEFAULT_ROOM_TYPE = RoomType.Single;
    private static final RoomType UPDATED_ROOM_TYPE = RoomType.Double;

    private static final PriceType DEFAULT_PRICE_TYPE = PriceType.Normal;
    private static final PriceType UPDATED_PRICE_TYPE = PriceType.Normal;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomService roomService;

    /**
     * This repository is mocked in the com.kyuan.hotel.repository.search test package.
     *
     * @see com.kyuan.hotel.repository.search.RoomSearchRepositoryMockConfiguration
     */
    @Autowired
    private RoomSearchRepository mockRoomSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRoomMockMvc;

    private Room room;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Room createEntity(EntityManager em) {
        Room room = new Room()
            .roomType(DEFAULT_ROOM_TYPE)
            .priceType(DEFAULT_PRICE_TYPE);
        return room;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Room createUpdatedEntity(EntityManager em) {
        Room room = new Room()
            .roomType(UPDATED_ROOM_TYPE)
            .priceType(UPDATED_PRICE_TYPE);
        return room;
    }

    @BeforeEach
    public void initTest() {
        room = createEntity(em);
    }

    @Test
    @Transactional
    public void createRoom() throws Exception {
        int databaseSizeBeforeCreate = roomRepository.findAll().size();
        // Create the Room
        restRoomMockMvc.perform(post("/api/rooms")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(room)))
            .andExpect(status().isCreated());

        // Validate the Room in the database
        List<Room> roomList = roomRepository.findAll();
        assertThat(roomList).hasSize(databaseSizeBeforeCreate + 1);
        Room testRoom = roomList.get(roomList.size() - 1);
        assertThat(testRoom.getRoomType()).isEqualTo(DEFAULT_ROOM_TYPE);
        assertThat(testRoom.getPriceType()).isEqualTo(DEFAULT_PRICE_TYPE);

        // Validate the Room in Elasticsearch
        verify(mockRoomSearchRepository, times(1)).save(testRoom);
    }

    @Test
    @Transactional
    public void createRoomWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = roomRepository.findAll().size();

        // Create the Room with an existing ID
        room.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRoomMockMvc.perform(post("/api/rooms")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(room)))
            .andExpect(status().isBadRequest());

        // Validate the Room in the database
        List<Room> roomList = roomRepository.findAll();
        assertThat(roomList).hasSize(databaseSizeBeforeCreate);

        // Validate the Room in Elasticsearch
        verify(mockRoomSearchRepository, times(0)).save(room);
    }


    @Test
    @Transactional
    public void getAllRooms() throws Exception {
        // Initialize the database
        roomRepository.saveAndFlush(room);

        // Get all the roomList
        restRoomMockMvc.perform(get("/api/rooms?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(room.getId().intValue())))
            .andExpect(jsonPath("$.[*].roomType").value(hasItem(DEFAULT_ROOM_TYPE.toString())))
            .andExpect(jsonPath("$.[*].priceType").value(hasItem(DEFAULT_PRICE_TYPE.toString())));
    }
    
    @Test
    @Transactional
    public void getRoom() throws Exception {
        // Initialize the database
        roomRepository.saveAndFlush(room);

        // Get the room
        restRoomMockMvc.perform(get("/api/rooms/{id}", room.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(room.getId().intValue()))
            .andExpect(jsonPath("$.roomType").value(DEFAULT_ROOM_TYPE.toString()))
            .andExpect(jsonPath("$.priceType").value(DEFAULT_PRICE_TYPE.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingRoom() throws Exception {
        // Get the room
        restRoomMockMvc.perform(get("/api/rooms/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRoom() throws Exception {
        // Initialize the database
        roomService.save(room);

        int databaseSizeBeforeUpdate = roomRepository.findAll().size();

        // Update the room
        Room updatedRoom = roomRepository.findById(room.getId()).get();
        // Disconnect from session so that the updates on updatedRoom are not directly saved in db
        em.detach(updatedRoom);
        updatedRoom
            .roomType(UPDATED_ROOM_TYPE)
            .priceType(UPDATED_PRICE_TYPE);

        restRoomMockMvc.perform(put("/api/rooms")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedRoom)))
            .andExpect(status().isOk());

        // Validate the Room in the database
        List<Room> roomList = roomRepository.findAll();
        assertThat(roomList).hasSize(databaseSizeBeforeUpdate);
        Room testRoom = roomList.get(roomList.size() - 1);
        assertThat(testRoom.getRoomType()).isEqualTo(UPDATED_ROOM_TYPE);
        assertThat(testRoom.getPriceType()).isEqualTo(UPDATED_PRICE_TYPE);

        // Validate the Room in Elasticsearch
        verify(mockRoomSearchRepository, times(2)).save(testRoom);
    }

    @Test
    @Transactional
    public void updateNonExistingRoom() throws Exception {
        int databaseSizeBeforeUpdate = roomRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRoomMockMvc.perform(put("/api/rooms")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(room)))
            .andExpect(status().isBadRequest());

        // Validate the Room in the database
        List<Room> roomList = roomRepository.findAll();
        assertThat(roomList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Room in Elasticsearch
        verify(mockRoomSearchRepository, times(0)).save(room);
    }

    @Test
    @Transactional
    public void deleteRoom() throws Exception {
        // Initialize the database
        roomService.save(room);

        int databaseSizeBeforeDelete = roomRepository.findAll().size();

        // Delete the room
        restRoomMockMvc.perform(delete("/api/rooms/{id}", room.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Room> roomList = roomRepository.findAll();
        assertThat(roomList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Room in Elasticsearch
        verify(mockRoomSearchRepository, times(1)).deleteById(room.getId());
    }

    @Test
    @Transactional
    public void searchRoom() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        roomService.save(room);
        when(mockRoomSearchRepository.search(queryStringQuery("id:" + room.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(room), PageRequest.of(0, 1), 1));

        // Search the room
        restRoomMockMvc.perform(get("/api/_search/rooms?query=id:" + room.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(room.getId().intValue())))
            .andExpect(jsonPath("$.[*].roomType").value(hasItem(DEFAULT_ROOM_TYPE.toString())))
            .andExpect(jsonPath("$.[*].priceType").value(hasItem(DEFAULT_PRICE_TYPE.toString())));
    }
}
