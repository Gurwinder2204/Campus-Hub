package com.campusstudyhub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

/**
 * Room entity representing bookable campus rooms.
 */
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Room name is required")
    @Column(nullable = false)
    private String name;

    @Positive(message = "Capacity must be positive")
    @Column(nullable = false)
    private int capacity;

    /** JSON string describing available resources (e.g. projector, whiteboard) */
    @Column(columnDefinition = "TEXT")
    private String resources;

    @Column(nullable = false)
    private String building;

    private String floor;

    @Column(name = "room_number")
    private String roomNumber;

    /** Optional JSON describing weekly availability windows */
    @Column(name = "availability_json", columnDefinition = "TEXT")
    private String availabilityJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId = "default";

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Constructors
    public Room() {
    }

    public Room(String name, int capacity, String building, String floor, String roomNumber) {
        this.name = name;
        this.capacity = capacity;
        this.building = building;
        this.floor = floor;
        this.roomNumber = roomNumber;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getResources() {
        return resources;
    }

    public void setResources(String resources) {
        this.resources = resources;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getAvailabilityJson() {
        return availabilityJson;
    }

    public void setAvailabilityJson(String availabilityJson) {
        this.availabilityJson = availabilityJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
