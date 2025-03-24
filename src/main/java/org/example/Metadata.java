package org.example;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Metadata {
    private String id;
    private String type;
    private double rating;
    private int reviews;

    public Metadata() {
    }

    public Metadata(String id, String type, double rating, int reviews) {
        this.id = id;
        this.type = type;
        this.rating = rating;
        this.reviews = reviews;
    }
}