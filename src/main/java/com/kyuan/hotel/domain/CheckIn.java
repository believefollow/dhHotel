package com.kyuan.hotel.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * A CheckIn.
 */
@Entity
@Table(name = "check_in")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "checkin")
public class CheckIn implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time")
    private LocalDate startTime;

    @Column(name = "end_time")
    private LocalDate endTime;

    @OneToMany(mappedBy = "checkIn")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Customer> customers = new HashSet<>();

    @OneToMany(mappedBy = "checkIn")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Bill> bills = new HashSet<>();

    @OneToMany(mappedBy = "checkIn")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Room> rooms = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartTime() {
        return startTime;
    }

    public CheckIn startTime(LocalDate startTime) {
        this.startTime = startTime;
        return this;
    }

    public void setStartTime(LocalDate startTime) {
        this.startTime = startTime;
    }

    public LocalDate getEndTime() {
        return endTime;
    }

    public CheckIn endTime(LocalDate endTime) {
        this.endTime = endTime;
        return this;
    }

    public void setEndTime(LocalDate endTime) {
        this.endTime = endTime;
    }

    public Set<Customer> getCustomers() {
        return customers;
    }

    public CheckIn customers(Set<Customer> customers) {
        this.customers = customers;
        return this;
    }

    public CheckIn addCustomer(Customer customer) {
        this.customers.add(customer);
        customer.setCheckIn(this);
        return this;
    }

    public CheckIn removeCustomer(Customer customer) {
        this.customers.remove(customer);
        customer.setCheckIn(null);
        return this;
    }

    public void setCustomers(Set<Customer> customers) {
        this.customers = customers;
    }

    public Set<Bill> getBills() {
        return bills;
    }

    public CheckIn bills(Set<Bill> bills) {
        this.bills = bills;
        return this;
    }

    public CheckIn addBill(Bill bill) {
        this.bills.add(bill);
        bill.setCheckIn(this);
        return this;
    }

    public CheckIn removeBill(Bill bill) {
        this.bills.remove(bill);
        bill.setCheckIn(null);
        return this;
    }

    public void setBills(Set<Bill> bills) {
        this.bills = bills;
    }

    public Set<Room> getRooms() {
        return rooms;
    }

    public CheckIn rooms(Set<Room> rooms) {
        this.rooms = rooms;
        return this;
    }

    public CheckIn addRoom(Room room) {
        this.rooms.add(room);
        room.setCheckIn(this);
        return this;
    }

    public CheckIn removeRoom(Room room) {
        this.rooms.remove(room);
        room.setCheckIn(null);
        return this;
    }

    public void setRooms(Set<Room> rooms) {
        this.rooms = rooms;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CheckIn)) {
            return false;
        }
        return id != null && id.equals(((CheckIn) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CheckIn{" +
            "id=" + getId() +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            "}";
    }
}
