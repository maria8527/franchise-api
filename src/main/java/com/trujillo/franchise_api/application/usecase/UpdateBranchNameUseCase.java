package com.trujillo.franchise_api.application.usecase;

import com.trujillo.franchise_api.domain.exception.ResourceNotFoundException;
import com.trujillo.franchise_api.domain.model.Branch;
import com.trujillo.franchise_api.domain.repository.FranchiseRepository;
import reactor.core.publisher.Mono;

public class UpdateBranchNameUseCase {

    private final FranchiseRepository repository;

    public UpdateBranchNameUseCase(FranchiseRepository repository) {
        this.repository = repository;
    }

    public Mono<Branch> execute(String franchiseId, String branchId, String newName) {
        Mono<Branch> branch = repository.findBranch(franchiseId, branchId)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("Branch not found: " + branchId)));

        return branch.flatMap(found -> {
            Branch updated = found.withName(newName);
            return repository.saveBranch(updated);
        });
    }
}
