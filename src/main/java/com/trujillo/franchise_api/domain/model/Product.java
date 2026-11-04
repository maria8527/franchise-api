package com.trujillo.franchise_api.domain.model;

public record Product(String id, String franchiseId, String branchId, String name, int stock) {

    public Product {
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
    }

    public Product withName(String newName) {
        return new Product(id, franchiseId, branchId, newName, stock);
    }

    public Product withStock(int newStock) {
        return new Product(id, franchiseId, branchId, name, newStock);
    }
}
