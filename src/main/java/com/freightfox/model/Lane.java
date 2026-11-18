package com.freightfox.model;

import jakarta.persistence.*;

@Entity
public class Lane {

    @Id
    private Long id;
    private String origin;
    private String destination;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
}
