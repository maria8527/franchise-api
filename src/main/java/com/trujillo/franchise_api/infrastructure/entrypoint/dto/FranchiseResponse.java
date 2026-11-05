package com.trujillo.franchise_api.infrastructure.entrypoint.dto;

import com.trujillo.franchise_api.domain.model.Franchise;

public record FranchiseResponse(String id, String name) {

    public static FranchiseResponse from(Franchise franchise) {
        return new FranchiseResponse(franchise.id(), franchise.name());
    }
}
