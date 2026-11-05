package com.trujillo.franchise_api.infrastructure.entrypoint.dto;

import com.trujillo.franchise_api.domain.model.BranchDetail;
import com.trujillo.franchise_api.domain.model.Product;

import java.util.ArrayList;
import java.util.List;

public record BranchDetailResponse(String id, String name, List<ProductResponse> products) {

    public static BranchDetailResponse from(BranchDetail detail) {
        List<ProductResponse> products = new ArrayList<>();
        for (Product product : detail.products()) {
            products.add(ProductResponse.from(product));
        }
        return new BranchDetailResponse(detail.branch().id(), detail.branch().name(), products);
    }
}
