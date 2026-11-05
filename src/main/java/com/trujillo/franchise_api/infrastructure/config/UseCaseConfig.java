package com.trujillo.franchise_api.infrastructure.config;

import com.trujillo.franchise_api.application.usecase.*;
import com.trujillo.franchise_api.domain.repository.FranchiseRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateFranchiseUseCase createFranchiseUseCase(FranchiseRepository repository) {
        return new CreateFranchiseUseCase(repository);
    }

    @Bean
    public UpdateFranchiseNameUseCase updateFranchiseNameUseCase(FranchiseRepository repository) {
        return new UpdateFranchiseNameUseCase(repository);
    }

    @Bean
    public GetTopStockProductsUseCase getTopStockProductsUseCase(FranchiseRepository repository) {
        return new GetTopStockProductsUseCase(repository);
    }

    @Bean
    public AddBranchUseCase addBranchUseCase(FranchiseRepository repository) {
        return new AddBranchUseCase(repository);
    }

    @Bean
    public UpdateBranchNameUseCase updateBranchNameUseCase(FranchiseRepository repository) {
        return new UpdateBranchNameUseCase(repository);
    }

    @Bean
    public AddProductUseCase addProductUseCase(FranchiseRepository repository) {
        return new AddProductUseCase(repository);
    }

    @Bean
    public DeleteProductUseCase deleteProductUseCase(FranchiseRepository repository) {
        return new DeleteProductUseCase(repository);
    }

    @Bean
    public UpdateProductStockUseCase updateProductStockUseCase(FranchiseRepository repository) {
        return new UpdateProductStockUseCase(repository);
    }

    @Bean
    public UpdateProductNameUseCase updateProductNameUseCase(FranchiseRepository repository) {
        return new UpdateProductNameUseCase(repository);
    }

    @Bean
    public ListFranchisesUseCase listFranchisesUseCase(FranchiseRepository repository) {
        return new ListFranchisesUseCase(repository);
    }

    @Bean
    public GetFranchiseDetailUseCase getFranchiseDetailUseCase(FranchiseRepository repository) {
        return new GetFranchiseDetailUseCase(repository);
    }
}
