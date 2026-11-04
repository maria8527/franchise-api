package com.trujillo.franchise_api.domain.model;

public record Branch(String id, String franchiseId, String name) {

    public Branch withName(String newName) {
        return new Branch(id, franchiseId, newName);
    }
}
