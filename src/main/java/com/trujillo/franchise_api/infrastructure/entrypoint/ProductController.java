package com.trujillo.franchise_api.infrastructure.entrypoint;

import com.trujillo.franchise_api.application.usecase.AddProductUseCase;
import com.trujillo.franchise_api.application.usecase.DeleteProductUseCase;
import com.trujillo.franchise_api.application.usecase.UpdateProductNameUseCase;
import com.trujillo.franchise_api.application.usecase.UpdateProductStockUseCase;
import com.trujillo.franchise_api.infrastructure.entrypoint.dto.CreateProductRequest;
import com.trujillo.franchise_api.infrastructure.entrypoint.dto.NameRequest;
import com.trujillo.franchise_api.infrastructure.entrypoint.dto.ProductResponse;
import com.trujillo.franchise_api.infrastructure.entrypoint.dto.UpdateStockRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/franchises/{franchiseId}/branches/{branchId}/products")
public class ProductController {

    private final AddProductUseCase addProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;
    private final UpdateProductNameUseCase updateProductNameUseCase;

    public ProductController(AddProductUseCase addProductUseCase, DeleteProductUseCase deleteProductUseCase, UpdateProductStockUseCase updateProductStockUseCase, UpdateProductNameUseCase updateProductNameUseCase) {
        this.addProductUseCase = addProductUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
        this.updateProductStockUseCase = updateProductStockUseCase;
        this.updateProductNameUseCase = updateProductNameUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProductResponse> add(@PathVariable String franchiseId, @PathVariable String branchId, @Valid @RequestBody CreateProductRequest request) {
        return addProductUseCase.execute(franchiseId, branchId, request.name(), request.stock()).map(ProductResponse::from);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String franchiseId, @PathVariable String branchId, @PathVariable String productId) {
        return deleteProductUseCase.execute(franchiseId, branchId, productId);
    }

    @PatchMapping("/{productId}/stock")
    public Mono<ProductResponse> updateStock(@PathVariable String franchiseId, @PathVariable String branchId, @PathVariable String productId, @Valid @RequestBody UpdateStockRequest request) {
        return updateProductStockUseCase.execute(franchiseId, branchId, productId, request.stock()).map(ProductResponse::from);
    }

    @PatchMapping("/{productId}")
    public Mono<ProductResponse> updateName(@PathVariable String franchiseId, @PathVariable String branchId, @PathVariable String productId, @Valid @RequestBody NameRequest request) {
        return updateProductNameUseCase.execute(franchiseId, branchId, productId, request.name()).map(ProductResponse::from);
    }
}
