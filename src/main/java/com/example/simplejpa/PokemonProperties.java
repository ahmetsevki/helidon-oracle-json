package com.example.simplejpa;

import jakarta.persistence.Embeddable;

@Embeddable
public class PokemonProperties {
    private String color;
    private Double speed;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }
}
