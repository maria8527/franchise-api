package com.trujillo.franchise_api.infrastructure.entrypoint;

import com.trujillo.franchise_api.application.usecase.AddBranchUseCase;
import com.trujillo.franchise_api.application.usecase.UpdateBranchNameUseCase;
import com.trujillo.franchise_api.infrastructure.entrypoint.dto.BranchResponse;
import com.trujillo.franchise_api.infrastructure.entrypoint.dto.NameRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/franchises/{franchiseId}/branches")
public class BranchController {

    private final AddBranchUseCase addBranchUseCase;
    private final UpdateBranchNameUseCase updateBranchNameUseCase;

    public BranchController(AddBranchUseCase addBranchUseCase, UpdateBranchNameUseCase updateBranchNameUseCase) {
        this.addBranchUseCase = addBranchUseCase;
        this.updateBranchNameUseCase = updateBranchNameUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BranchResponse> add(@PathVariable String franchiseId, @Valid @RequestBody NameRequest request) {
        return addBranchUseCase.execute(franchiseId, request.name()).map(BranchResponse::from);
    }

    @PatchMapping("/{branchId}")
    public Mono<BranchResponse> updateName(@PathVariable String franchiseId, @PathVariable String branchId, @Valid @RequestBody NameRequest request) {
        return updateBranchNameUseCase.execute(franchiseId, branchId, request.name()).map(BranchResponse::from);
    }
}
