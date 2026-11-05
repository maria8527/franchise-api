package com.trujillo.franchise_api.application.usecase;

import com.trujillo.franchise_api.domain.exception.ResourceNotFoundException;
import com.trujillo.franchise_api.domain.model.*;
import com.trujillo.franchise_api.domain.repository.FranchiseRepository;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class GetFranchiseDetailUseCase {

    private final FranchiseRepository repository;

    public GetFranchiseDetailUseCase(FranchiseRepository repository) {
        this.repository = repository;
    }

    public Mono<FranchiseDetail> execute(String franchiseId) {
        // 1. Buscar la franquicia; si no existe, error
        Mono<Franchise> franchise = repository.findFranchiseById(franchiseId).switchIfEmpty(Mono.error(new ResourceNotFoundException("Franchise not found: " + franchiseId)));

        // 2. Si existe, traer sucursales y productos al mismo tiempo y armar el detalle
        return franchise.flatMap(found -> {
            Mono<List<Branch>> branches = repository.findBranchesByFranchise(franchiseId).collectList();
            Mono<List<Product>> products = repository.findProductsByFranchise(franchiseId).collectList();

            return Mono.zip(branches, products).map(lists -> buildDetail(found, lists.getT1(), lists.getT2()));
        });
    }

    // Java normal: agrupa los productos dentro de la sucursal a la que pertenecen
    private FranchiseDetail buildDetail(Franchise franchise, List<Branch> branches, List<Product> products) {
        List<BranchDetail> branchDetails = new ArrayList<>();

        for (Branch branch : branches) {
            List<Product> branchProducts = new ArrayList<>();
            for (Product product : products) {
                if (product.branchId().equals(branch.id())) {
                    branchProducts.add(product);
                }
            }
            branchDetails.add(new BranchDetail(branch, branchProducts));
        }

        return new FranchiseDetail(franchise, branchDetails);
    }
}
