package com.trujillo.franchise_api.application.usecase;

import com.trujillo.franchise_api.domain.model.Franchise;
import com.trujillo.franchise_api.domain.repository.FranchiseRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class CreateFranchiseUseCase {

    private final FranchiseRepository repository;

    public CreateFranchiseUseCase(FranchiseRepository repository) {
        this.repository = repository;
    }

    public Mono<Franchise> execute(String name) {
        String id = UUID.randomUUID().toString();
        Franchise franchise = new Franchise(id, name);
        return repository.saveFranchise(franchise);
    }
}
