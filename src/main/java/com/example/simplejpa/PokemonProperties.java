package com.example.simplejpa;

import jakarta.persistence.Embeddable;

@Embeddable
public class PokemonProperties {
    private String color;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
