package com.kyuan.hotel.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;

import com.kyuan.hotel.domain.enumeration.RoomType;

import com.kyuan.hotel.domain.enumeration.PriceType;

/**
 * A Room.
 */
@Entity
@Table(name = "room")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "room")
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type")
    private RoomType roomType;

    @Enumerated(EnumType.STRING)
    @Column(name = "price_type")
    private PriceType priceType;

    @ManyToOne
    @JsonIgnoreProperties(value = "rooms", allowSetters = true)
    private CheckIn checkIn;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public Room roomType(RoomType roomType) {
        this.roomType = roomType;
        return this;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public PriceType getPriceType() {
        return priceType;
    }

    public Room priceType(PriceType priceType) {
        this.priceType = priceType;
        return this;
    }

    public void setPriceType(PriceType priceType) {
        this.priceType = priceType;
    }

    public CheckIn getCheckIn() {
        return checkIn;
    }

    public Room checkIn(CheckIn checkIn) {
        this.checkIn = checkIn;
        return this;
    }

    public void setCheckIn(CheckIn checkIn) {
        this.checkIn = checkIn;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Room)) {
            return false;
        }
        return id != null && id.equals(((Room) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Room{" +
            "id=" + getId() +
            ", roomType='" + getRoomType() + "'" +
            ", priceType='" + getPriceType() + "'" +
            "}";
    }
}
