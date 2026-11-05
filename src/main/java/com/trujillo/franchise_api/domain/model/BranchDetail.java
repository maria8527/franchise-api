package com.trujillo.franchise_api.domain.model;

import java.util.List;

public record BranchDetail(Branch branch, List<Product> products) {
}
