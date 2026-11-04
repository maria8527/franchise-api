package com.trujillo.franchise_api.domain.model;

public record Franchise(String id, String name) {

    public Franchise withName(String newName) {
        return new Franchise(id, newName);
    }
}