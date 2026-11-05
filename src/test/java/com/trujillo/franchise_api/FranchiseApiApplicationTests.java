package com.trujillo.franchise_api;

import com.trujillo.franchise_api.application.usecase.*;
import com.trujillo.franchise_api.domain.exception.ResourceNotFoundException;
import com.trujillo.franchise_api.domain.model.Branch;
import com.trujillo.franchise_api.domain.model.BranchTopProduct;
import com.trujillo.franchise_api.domain.model.Franchise;
import com.trujillo.franchise_api.domain.model.Product;
import com.trujillo.franchise_api.domain.repository.FranchiseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateFranchiseUseCaseTest {

    @Mock
    private FranchiseRepository repository;

    @InjectMocks
    private CreateFranchiseUseCase useCase;

    @InjectMocks
    private AddBranchUseCase addBranchUseCase;

    @InjectMocks
    private AddProductUseCase addProductUseCase;

    @InjectMocks
    private DeleteProductUseCase deleteProductUseCase;

    @InjectMocks
    private UpdateProductStockUseCase updateProductStockUseCase;

    @InjectMocks
    private GetTopStockProductsUseCase getTopStockProductsUseCase;

    @InjectMocks
    private UpdateFranchiseNameUseCase updateFranchiseNameUseCase;

    @InjectMocks
    private UpdateBranchNameUseCase updateBranchNameUseCase;

    @InjectMocks
    private UpdateProductNameUseCase updateProductNameUseCase;

    @Test
    void execute_shouldSaveFranchiseAndReturnIt() {
        // Arrange: el repositorio falso devuelve esta franquicia al guardar
        Franchise savedFranchise = new Franchise("1", "Servientrega");
        when(repository.saveFranchise(any(Franchise.class))).thenReturn(Mono.just(savedFranchise));

        // Act + Assert: ejecutamos y verificamos el resultado reactivo
        StepVerifier.create(useCase.execute("Servientrega")).expectNext(savedFranchise).verifyComplete();

        // Assert extra: verificamos qué le pasó el caso de uso al repositorio
        ArgumentCaptor<Franchise> captor = ArgumentCaptor.forClass(Franchise.class);
        verify(repository).saveFranchise(captor.capture());
        assertNotNull(captor.getValue().id());
        assertEquals("Servientrega", captor.getValue().name());
    }

    @Test
    void execute_shouldPropagateErrorWhenRepositoryFails() {
        when(repository.saveFranchise(any(Franchise.class))).thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(useCase.execute("Servientrega")).expectErrorMessage("DB error").verify();
    }

    // ---------- AddBranchUseCase ----------

    @Test
    void addBranch_shouldSaveBranchWhenFranchiseExists() {
        Franchise franchise = new Franchise("f1", "Burger Corp");
        Branch savedBranch = new Branch("b1", "f1", "Centro");
        when(repository.findFranchiseById("f1")).thenReturn(Mono.just(franchise));
        when(repository.saveBranch(any(Branch.class))).thenReturn(Mono.just(savedBranch));

        StepVerifier.create(addBranchUseCase.execute("f1", "Centro")).expectNext(savedBranch).verifyComplete();

        ArgumentCaptor<Branch> captor = ArgumentCaptor.forClass(Branch.class);
        verify(repository).saveBranch(captor.capture());
        assertEquals("f1", captor.getValue().franchiseId());
        assertEquals("Centro", captor.getValue().name());
    }

    @Test
    void addBranch_shouldFailWhenFranchiseNotFound() {
        when(repository.findFranchiseById("f1")).thenReturn(Mono.empty());

        StepVerifier.create(addBranchUseCase.execute("f1", "Centro")).expectError(ResourceNotFoundException.class).verify();

        verify(repository, never()).saveBranch(any(Branch.class));
    }

    // ---------- AddProductUseCase ----------

    @Test
    void addProduct_shouldSaveProductWhenBranchExists() {
        Branch branch = new Branch("b1", "f1", "Centro");
        Product savedProduct = new Product("p1", "f1", "b1", "Hamburguesa", 10);
        when(repository.findBranch("f1", "b1")).thenReturn(Mono.just(branch));
        when(repository.saveProduct(any(Product.class))).thenReturn(Mono.just(savedProduct));

        StepVerifier.create(addProductUseCase.execute("f1", "b1", "Hamburguesa", 10)).expectNext(savedProduct).verifyComplete();

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(repository).saveProduct(captor.capture());
        assertEquals("b1", captor.getValue().branchId());
        assertEquals(10, captor.getValue().stock());
    }

    @Test
    void addProduct_shouldFailWhenBranchNotFound() {
        when(repository.findBranch("f1", "b1")).thenReturn(Mono.empty());

        StepVerifier.create(addProductUseCase.execute("f1", "b1", "Hamburguesa", 10)).expectError(ResourceNotFoundException.class).verify();

        verify(repository, never()).saveProduct(any(Product.class));
    }

    @Test
    void addProduct_shouldFailWhenStockIsNegative() {
        Branch branch = new Branch("b1", "f1", "Centro");
        when(repository.findBranch("f1", "b1")).thenReturn(Mono.just(branch));

        StepVerifier.create(addProductUseCase.execute("f1", "b1", "Hamburguesa", -5)).expectError(IllegalArgumentException.class).verify();

        verify(repository, never()).saveProduct(any(Product.class));
    }

    // ---------- DeleteProductUseCase ----------

    @Test
    void deleteProduct_shouldDeleteWhenProductExists() {
        Product product = new Product("p1", "f1", "b1", "Hamburguesa", 10);
        when(repository.findProduct("f1", "b1", "p1")).thenReturn(Mono.just(product));
        when(repository.deleteProduct("f1", "b1", "p1")).thenReturn(Mono.empty());

        StepVerifier.create(deleteProductUseCase.execute("f1", "b1", "p1")).verifyComplete();

        verify(repository).deleteProduct("f1", "b1", "p1");
    }

    @Test
    void deleteProduct_shouldFailWhenProductNotFound() {
        when(repository.findProduct("f1", "b1", "p1")).thenReturn(Mono.empty());

        StepVerifier.create(deleteProductUseCase.execute("f1", "b1", "p1")).expectError(ResourceNotFoundException.class).verify();

        verify(repository, never()).deleteProduct("f1", "b1", "p1");
    }

    // ---------- UpdateProductStockUseCase ----------

    @Test
    void updateStock_shouldSaveProductWithNewStock() {
        Product product = new Product("p1", "f1", "b1", "Hamburguesa", 10);
        Product updated = new Product("p1", "f1", "b1", "Hamburguesa", 25);
        when(repository.findProduct("f1", "b1", "p1")).thenReturn(Mono.just(product));
        when(repository.saveProduct(any(Product.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(updateProductStockUseCase.execute("f1", "b1", "p1", 25)).expectNext(updated).verifyComplete();

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(repository).saveProduct(captor.capture());
        assertEquals(25, captor.getValue().stock());
        assertEquals("Hamburguesa", captor.getValue().name());
    }

    @Test
    void updateStock_shouldFailWhenProductNotFound() {
        when(repository.findProduct("f1", "b1", "p1")).thenReturn(Mono.empty());

        StepVerifier.create(updateProductStockUseCase.execute("f1", "b1", "p1", 25)).expectError(ResourceNotFoundException.class).verify();

        verify(repository, never()).saveProduct(any(Product.class));
    }

    @Test
    void updateStock_shouldFailWhenNewStockIsNegative() {
        Product product = new Product("p1", "f1", "b1", "Hamburguesa", 10);
        when(repository.findProduct("f1", "b1", "p1")).thenReturn(Mono.just(product));

        StepVerifier.create(updateProductStockUseCase.execute("f1", "b1", "p1", -1)).expectError(IllegalArgumentException.class).verify();

        verify(repository, never()).saveProduct(any(Product.class));
    }

    // ---------- GetTopStockProductsUseCase ----------

    @Test
    void topStock_shouldReturnProductWithMostStockPerBranch() {
        Franchise franchise = new Franchise("f1", "Burger Corp");
        Branch centro = new Branch("b1", "f1", "Centro");
        Branch norte = new Branch("b2", "f1", "Norte");
        Product hamburguesa = new Product("p1", "f1", "b1", "Hamburguesa", 10);
        Product papas = new Product("p2", "f1", "b1", "Papas", 30);
        Product gaseosa = new Product("p3", "f1", "b2", "Gaseosa", 5);

        when(repository.findFranchiseById("f1")).thenReturn(Mono.just(franchise));
        when(repository.findBranchesByFranchise("f1")).thenReturn(Flux.just(centro, norte));
        when(repository.findProductsByFranchise("f1")).thenReturn(Flux.just(hamburguesa, papas, gaseosa));

        StepVerifier.create(getTopStockProductsUseCase.execute("f1")).expectNext(new BranchTopProduct("b1", "Centro", "p2", "Papas", 30)).expectNext(new BranchTopProduct("b2", "Norte", "p3", "Gaseosa", 5)).verifyComplete();
    }

    @Test
    void topStock_shouldSkipBranchesWithoutProducts() {
        Franchise franchise = new Franchise("f1", "Burger Corp");
        Branch centro = new Branch("b1", "f1", "Centro");
        Branch norte = new Branch("b2", "f1", "Norte");
        Product papas = new Product("p2", "f1", "b1", "Papas", 30);

        when(repository.findFranchiseById("f1")).thenReturn(Mono.just(franchise));
        when(repository.findBranchesByFranchise("f1")).thenReturn(Flux.just(centro, norte));
        when(repository.findProductsByFranchise("f1")).thenReturn(Flux.just(papas));

        StepVerifier.create(getTopStockProductsUseCase.execute("f1")).expectNext(new BranchTopProduct("b1", "Centro", "p2", "Papas", 30)).verifyComplete();
    }

    @Test
    void topStock_shouldFailWhenFranchiseNotFound() {
        when(repository.findFranchiseById("f1")).thenReturn(Mono.empty());

        StepVerifier.create(getTopStockProductsUseCase.execute("f1")).expectError(ResourceNotFoundException.class).verify();
    }

    // ---------- UpdateFranchiseNameUseCase ----------

    @Test
    void updateFranchiseName_shouldSaveFranchiseWithNewName() {
        Franchise franchise = new Franchise("f1", "Burger Corp");
        Franchise updated = new Franchise("f1", "Burger Master");
        when(repository.findFranchiseById("f1")).thenReturn(Mono.just(franchise));
        when(repository.saveFranchise(any(Franchise.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(updateFranchiseNameUseCase.execute("f1", "Burger Master"))
                .expectNext(updated)
                .verifyComplete();

        ArgumentCaptor<Franchise> captor = ArgumentCaptor.forClass(Franchise.class);
        verify(repository).saveFranchise(captor.capture());
        assertEquals("f1", captor.getValue().id());
        assertEquals("Burger Master", captor.getValue().name());
    }

    @Test
    void updateFranchiseName_shouldFailWhenFranchiseNotFound() {
        when(repository.findFranchiseById("f1")).thenReturn(Mono.empty());

        StepVerifier.create(updateFranchiseNameUseCase.execute("f1", "Burger Master"))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(repository, never()).saveFranchise(any(Franchise.class));
    }

    // ---------- UpdateBranchNameUseCase ----------

    @Test
    void updateBranchName_shouldSaveBranchWithNewName() {
        Branch branch = new Branch("b1", "f1", "Centro");
        Branch updated = new Branch("b1", "f1", "Centro Histórico");
        when(repository.findBranch("f1", "b1")).thenReturn(Mono.just(branch));
        when(repository.saveBranch(any(Branch.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(updateBranchNameUseCase.execute("f1", "b1", "Centro Histórico"))
                .expectNext(updated)
                .verifyComplete();

        ArgumentCaptor<Branch> captor = ArgumentCaptor.forClass(Branch.class);
        verify(repository).saveBranch(captor.capture());
        assertEquals("b1", captor.getValue().id());
        assertEquals("Centro Histórico", captor.getValue().name());
    }

    @Test
    void updateBranchName_shouldFailWhenBranchNotFound() {
        when(repository.findBranch("f1", "b1")).thenReturn(Mono.empty());

        StepVerifier.create(updateBranchNameUseCase.execute("f1", "b1", "Centro Histórico"))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(repository, never()).saveBranch(any(Branch.class));
    }

    // ---------- UpdateProductNameUseCase ----------

    @Test
    void updateProductName_shouldSaveProductWithNewNameAndSameStock() {
        Product product = new Product("p1", "f1", "b1", "Hamburguesa", 10);
        Product updated = new Product("p1", "f1", "b1", "Hamburguesa Doble", 10);
        when(repository.findProduct("f1", "b1", "p1")).thenReturn(Mono.just(product));
        when(repository.saveProduct(any(Product.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(updateProductNameUseCase.execute("f1", "b1", "p1", "Hamburguesa Doble"))
                .expectNext(updated)
                .verifyComplete();

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(repository).saveProduct(captor.capture());
        assertEquals("Hamburguesa Doble", captor.getValue().name());
        assertEquals(10, captor.getValue().stock());
    }

    @Test
    void updateProductName_shouldFailWhenProductNotFound() {
        when(repository.findProduct("f1", "b1", "p1")).thenReturn(Mono.empty());

        StepVerifier.create(updateProductNameUseCase.execute("f1", "b1", "p1", "Hamburguesa Doble"))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(repository, never()).saveProduct(any(Product.class));
    }
}
