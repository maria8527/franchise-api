package com.trujillo.franchise_api.infrastructure.entrypoint.dto;

import com.trujillo.franchise_api.domain.model.BranchTopProduct;

public record BranchTopProductResponse(String branchId, String branchName, String productId, String productName,
                                       int stock) {

    public static BranchTopProductResponse from(BranchTopProduct top) {
        return new BranchTopProductResponse(top.branchId(), top.branchName(), top.productId(), top.productName(), top.stock());
    }
}
