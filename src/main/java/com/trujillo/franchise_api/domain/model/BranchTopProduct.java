package com.trujillo.franchise_api.domain.model;

public record BranchTopProduct(String branchId, String branchName, String productId, String productName, int stock) {
}