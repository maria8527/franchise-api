package com.trujillo.franchise_api.domain.repository;

import com.trujillo.franchise_api.domain.model.Branch;
import com.trujillo.franchise_api.domain.model.Franchise;
import com.trujillo.franchise_api.domain.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Contrato: los métodos find devuelven Mono vacío cuando no encuentran el recurso.
public interface FranchiseRepository {

    Mono<Franchise> saveFranchise(Franchise franchise);

    Mono<Franchise> findFranchiseById(String franchiseId);

    Mono<Branch> saveBranch(Branch branch);

    Mono<Branch> findBranch(String franchiseId, String branchId);

    Flux<Branch> findBranchesByFranchise(String franchiseId);

    Mono<Product> saveProduct(Product product);

    Mono<Product> findProduct(String franchiseId, String branchId, String productId);

    Mono<Void> deleteProduct(String franchiseId, String branchId, String productId);

    Flux<Product> findProductsByFranchise(String franchiseId);
}
