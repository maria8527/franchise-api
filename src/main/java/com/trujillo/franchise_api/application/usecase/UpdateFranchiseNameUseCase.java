package com.trujillo.franchise_api.application.usecase;

import com.trujillo.franchise_api.domain.exception.ResourceNotFoundException;
import com.trujillo.franchise_api.domain.model.Franchise;
import com.trujillo.franchise_api.domain.repository.FranchiseRepository;
import reactor.core.publisher.Mono;

public class UpdateFranchiseNameUseCase {

    private final FranchiseRepository repository;

    public UpdateFranchiseNameUseCase(FranchiseRepository repository) {
        this.repository = repository;
    }

    public Mono<Franchise> execute(String franchiseId, String newName) {
        Mono<Franchise> franchise = repository.findFranchiseById(franchiseId)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("Franchise not found: " + franchiseId)));

        return franchise.flatMap(found -> {
            Franchise updated = found.withName(newName);
            return repository.saveFranchise(updated);
        });
    }
}
