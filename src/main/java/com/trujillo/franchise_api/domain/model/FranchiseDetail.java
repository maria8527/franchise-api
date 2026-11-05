package com.trujillo.franchise_api.domain.model;

import java.util.List;

public record FranchiseDetail(Franchise franchise, List<BranchDetail> branches) {
}
