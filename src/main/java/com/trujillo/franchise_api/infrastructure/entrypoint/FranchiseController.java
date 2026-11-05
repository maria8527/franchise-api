package com.trujillo.franchise_api.infrastructure.entrypoint;

import com.trujillo.franchise_api.application.usecase.CreateFranchiseUseCase;
import com.trujillo.franchise_api.application.usecase.GetTopStockProductsUseCase;
import com.trujillo.franchise_api.application.usecase.UpdateFranchiseNameUseCase;
import com.trujillo.franchise_api.infrastructure.entrypoint.dto.BranchTopProductResponse;
import com.trujillo.franchise_api.infrastructure.entrypoint.dto.FranchiseResponse;
import com.trujillo.franchise_api.infrastructure.entrypoint.dto.NameRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/franchises")
public class FranchiseController {

    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final UpdateFranchiseNameUseCase updateFranchiseNameUseCase;
    private final GetTopStockProductsUseCase getTopStockProductsUseCase;

    public FranchiseController(CreateFranchiseUseCase createFranchiseUseCase,
                               UpdateFranchiseNameUseCase updateFranchiseNameUseCase,
                               GetTopStockProductsUseCase getTopStockProductsUseCase) {
        this.createFranchiseUseCase = createFranchiseUseCase;
        this.updateFranchiseNameUseCase = updateFranchiseNameUseCase;
        this.getTopStockProductsUseCase = getTopStockProductsUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<FranchiseResponse> create(@Valid @RequestBody NameRequest request) {
        return createFranchiseUseCase.execute(request.name())
                .map(FranchiseResponse::from);
    }

    @PatchMapping("/{franchiseId}")
    public Mono<FranchiseResponse> updateName(@PathVariable String franchiseId,
                                              @Valid @RequestBody NameRequest request) {
        return updateFranchiseNameUseCase.execute(franchiseId, request.name())
                .map(FranchiseResponse::from);
    }

    @GetMapping("/{franchiseId}/top-stock-products")
    public Flux<BranchTopProductResponse> getTopStockProducts(@PathVariable String franchiseId) {
        return getTopStockProductsUseCase.execute(franchiseId)
                .map(BranchTopProductResponse::from);
    }
}
