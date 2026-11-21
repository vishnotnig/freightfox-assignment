package com.freightfox.model;

import jakarta.persistence.*;

@Entity
public class TransporterQuote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Lane lane;

    @ManyToOne
    private Transporter transporter;

    private int cost;

    public Long getId() { return id; }
    public Lane getLane() { return lane; }
    public void setLane(Lane lane) { this.lane = lane; }
    public Transporter getTransporter() { return transporter; }
    public void setTransporter(Transporter transporter) { this.transporter = transporter; }
    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }
}
