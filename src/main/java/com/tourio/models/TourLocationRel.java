package com.tourio.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "tour_location_rel")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TourLocationRel implements Serializable {
    @EmbeddedId
    private TourLocationRelID id = new TourLocationRelID();

    @ManyToOne
    @MapsId("tourId")
    @JoinColumn(name = "tour_id")
    private Tour tour;

    @ManyToOne
    @MapsId("locationId")
    @JoinColumn(name = "location_id")
    private Location location;

    private Long sequence;

    public TourLocationRel(Tour tour, Location location, Long sequence) {
        this.id = new TourLocationRelID(tour.getId(), location.getId());
        this.tour = tour;
        this.location = location;
        this.sequence = sequence;
    }
}