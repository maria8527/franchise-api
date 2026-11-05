package com.trujillo.franchise_api.application.usecase;

import com.trujillo.franchise_api.domain.exception.ResourceNotFoundException;
import com.trujillo.franchise_api.domain.model.Product;
import com.trujillo.franchise_api.domain.repository.FranchiseRepository;
import reactor.core.publisher.Mono;

public class DeleteProductUseCase {

    private final FranchiseRepository repository;

    public DeleteProductUseCase(FranchiseRepository repository) {
        this.repository = repository;
    }

    public Mono<Void> execute(String franchiseId, String branchId, String productId) {
        // 1. Buscar el producto; si no existe, error
        Mono<Product> product = repository.findProduct(franchiseId, branchId, productId)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("Product not found: " + productId)));

        // 2. Si existe, eliminarlo
        return product.flatMap(found ->
                repository.deleteProduct(franchiseId, branchId, productId));
    }
}
