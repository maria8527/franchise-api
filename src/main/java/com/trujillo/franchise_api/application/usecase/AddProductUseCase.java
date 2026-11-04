package com.trujillo.franchise_api.application.usecase;

import com.trujillo.franchise_api.domain.exception.ResourceNotFoundException;
import com.trujillo.franchise_api.domain.model.Branch;
import com.trujillo.franchise_api.domain.model.Product;
import com.trujillo.franchise_api.domain.repository.FranchiseRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class AddProductUseCase {

    private final FranchiseRepository repository;

    public AddProductUseCase(FranchiseRepository repository) {
        this.repository = repository;
    }

    public Mono<Product> execute(String franchiseId, String branchId, String name, int stock) {
        // 1. Buscar la sucursal dentro de la franquicia
        Mono<Branch> branch = repository.findBranch(franchiseId, branchId)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("Branch not found: " + branchId)));

        // 2. Cuando exista, crear el producto y guardarlo
        return branch.flatMap(found -> {
            String id = UUID.randomUUID().toString();
            Product product = new Product(id, franchiseId, found.id(), name, stock);
            return repository.saveProduct(product);
        });
    }
}
