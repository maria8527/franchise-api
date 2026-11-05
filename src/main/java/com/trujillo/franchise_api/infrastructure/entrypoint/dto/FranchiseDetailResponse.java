package com.trujillo.franchise_api.infrastructure.entrypoint.dto;

import com.trujillo.franchise_api.domain.model.BranchDetail;
import com.trujillo.franchise_api.domain.model.FranchiseDetail;

import java.util.ArrayList;
import java.util.List;

public record FranchiseDetailResponse(String id, String name, List<BranchDetailResponse> branches) {

    public static FranchiseDetailResponse from(FranchiseDetail detail) {
        List<BranchDetailResponse> branches = new ArrayList<>();
        for (BranchDetail branchDetail : detail.branches()) {
            branches.add(BranchDetailResponse.from(branchDetail));
        }
        return new FranchiseDetailResponse(detail.franchise().id(), detail.franchise().name(), branches);
    }
}
