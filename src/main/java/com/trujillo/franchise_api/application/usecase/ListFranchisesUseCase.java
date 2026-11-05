package com.trujillo.franchise_api.application.usecase;

import com.trujillo.franchise_api.domain.model.Franchise;
import com.trujillo.franchise_api.domain.repository.FranchiseRepository;
import reactor.core.publisher.Flux;

public class ListFranchisesUseCase {

    private final FranchiseRepository repository;

    public ListFranchisesUseCase(FranchiseRepository repository) {
        this.repository = repository;
    }

    public Flux<Franchise> execute() {
        return repository.findAllFranchises();
    }

}
