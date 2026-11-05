package com.trujillo.franchise_api.application.usecase;

import com.trujillo.franchise_api.domain.exception.ResourceNotFoundException;
import com.trujillo.franchise_api.domain.model.Branch;
import com.trujillo.franchise_api.domain.model.BranchTopProduct;
import com.trujillo.franchise_api.domain.model.Franchise;
import com.trujillo.franchise_api.domain.model.Product;
import com.trujillo.franchise_api.domain.repository.FranchiseRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class GetTopStockProductsUseCase {

    private final FranchiseRepository repository;

    public GetTopStockProductsUseCase(FranchiseRepository repository) {
        this.repository = repository;
    }

    public Flux<BranchTopProduct> execute(String franchiseId) {
        // 1. Validar que la franquicia existe
        Mono<Franchise> franchise = repository.findFranchiseById(franchiseId).switchIfEmpty(Mono.error(new ResourceNotFoundException("Franchise not found: " + franchiseId)));

        // 2. Si existe, calcular los productos con más stock
        return franchise.flatMapMany(found -> findTopProducts(franchiseId));
    }

    private Flux<BranchTopProduct> findTopProducts(String franchiseId) {
        // Convertimos cada Flux en un Mono con la lista completa
        Mono<List<Branch>> branches = repository.findBranchesByFranchise(franchiseId).collectList();
        Mono<List<Product>> products = repository.findProductsByFranchise(franchiseId).collectList();

        // Esperamos las dos listas, hacemos el cálculo y devolvemos un Flux
        return Mono.zip(branches, products).flatMapMany(lists -> {
            List<Branch> branchList = lists.getT1();
            List<Product> productList = lists.getT2();
            return Flux.fromIterable(calculateTopProducts(branchList, productList));
        });
    }

    // Lógica de negocio en Java normal, sin nada reactivo
    private List<BranchTopProduct> calculateTopProducts(List<Branch> branches, List<Product> products) {
        List<BranchTopProduct> result = new ArrayList<>();

        for (Branch branch : branches) {
            Product top = null;

            for (Product product : products) {
                boolean belongsToBranch = product.branchId().equals(branch.id());
                if (belongsToBranch && (top == null || product.stock() > top.stock())) {
                    top = product;
                }
            }

            if (top != null) {
                result.add(new BranchTopProduct(branch.id(), branch.name(), top.id(), top.name(), top.stock()));
            }
        }
        return result;
    }
}
