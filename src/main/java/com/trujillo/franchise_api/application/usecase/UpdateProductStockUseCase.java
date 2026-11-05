package com.trujillo.franchise_api.application.usecase;

import com.trujillo.franchise_api.domain.exception.ResourceNotFoundException;
import com.trujillo.franchise_api.domain.model.Product;
import com.trujillo.franchise_api.domain.repository.FranchiseRepository;
import reactor.core.publisher.Mono;

public class UpdateProductStockUseCase {
    private final FranchiseRepository repository;

    public UpdateProductStockUseCase(FranchiseRepository repository) {
        this.repository = repository;
    }

    public Mono<Product> execute(String franchiseId, String branchId, String productId, int newStock) {
        // 1. Buscar el producto; si no existe, error
        Mono<Product> product = repository.findProduct(franchiseId, branchId, productId).switchIfEmpty(Mono.error(new ResourceNotFoundException("Product not found: " + productId)));

        // 2. Crear una copia con el nuevo stock y guardarla
        return product.flatMap(found -> {
            Product updated = found.withStock(newStock);
            return repository.saveProduct(updated);
        });
    }
}
