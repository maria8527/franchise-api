package com.trujillo.franchise_api.application.usecase;

import com.trujillo.franchise_api.domain.exception.ResourceNotFoundException;
import com.trujillo.franchise_api.domain.model.Branch;
import com.trujillo.franchise_api.domain.model.Franchise;
import com.trujillo.franchise_api.domain.repository.FranchiseRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class AddBranchUseCase {

    private final FranchiseRepository repository;

    public AddBranchUseCase(FranchiseRepository repository) {
        this.repository = repository;
    }

    public Mono<Branch> execute(String franchiseId, String name) {
        // 1. Buscar la franquicia; si no existe, convertir el vacío en un error
        Mono<Franchise> franchise = repository.findFranchiseById(franchiseId)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("Franchise not found: " + franchiseId)));

        // 2. Cuando la franquicia exista, crear la sucursal y guardarla
        return franchise.flatMap(found -> {
            String id = UUID.randomUUID().toString();
            Branch branch = new Branch(id, found.id(), name);
            return repository.saveBranch(branch);
        });
    }
}

