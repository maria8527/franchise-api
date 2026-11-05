package com.trujillo.franchise_api.application.usecase;

import com.trujillo.franchise_api.domain.exception.ResourceNotFoundException;
import com.trujillo.franchise_api.domain.model.Product;
import com.trujillo.franchise_api.domain.repository.FranchiseRepository;
import reactor.core.publisher.Mono;

public class UpdateProductNameUseCase {

    private final FranchiseRepository repository;

    public UpdateProductNameUseCase(FranchiseRepository repository) {
        this.repository = repository;
    }

    public Mono<Product> execute(String franchiseId, String branchId, String productId, String newName) {
        Mono<Product> product = repository.findProduct(franchiseId, branchId, productId).switchIfEmpty(Mono.error(new ResourceNotFoundException("Product not found: " + productId)));

        return product.flatMap(found -> {
            Product updated = found.withName(newName);
            return repository.saveProduct(updated);
        });
    }
}
