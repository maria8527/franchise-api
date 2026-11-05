package com.trujillo.franchise_api.infrastructure.entrypoint.dto;

import com.trujillo.franchise_api.domain.model.Branch;

public record BranchResponse(String id, String franchiseId, String name) {

    public static BranchResponse from(Branch branch) {
        return new BranchResponse(branch.id(), branch.franchiseId(), branch.name());
    }
}
