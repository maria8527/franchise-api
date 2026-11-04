package com.trujillo.franchise_api;

import com.trujillo.franchise_api.application.usecase.AddBranchUseCase;
import com.trujillo.franchise_api.application.usecase.AddProductUseCase;
import com.trujillo.franchise_api.application.usecase.CreateFranchiseUseCase;
import com.trujillo.franchise_api.domain.exception.ResourceNotFoundException;
import com.trujillo.franchise_api.domain.model.Branch;
import com.trujillo.franchise_api.domain.model.Franchise;
import com.trujillo.franchise_api.domain.model.Product;
import com.trujillo.franchise_api.domain.repository.FranchiseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

    @Test
    void execute_shouldSaveFranchiseAndReturnIt() {
        // Arrange: el repositorio falso devuelve esta franquicia al guardar
        Franchise savedFranchise = new Franchise("1", "Servientrega");
        when(repository.saveFranchise(any(Franchise.class)))
                .thenReturn(Mono.just(savedFranchise));

        // Act + Assert: ejecutamos y verificamos el resultado reactivo
        StepVerifier.create(useCase.execute("Servientrega"))
                .expectNext(savedFranchise)
                .verifyComplete();

        // Assert extra: verificamos qué le pasó el caso de uso al repositorio
        ArgumentCaptor<Franchise> captor = ArgumentCaptor.forClass(Franchise.class);
        verify(repository).saveFranchise(captor.capture());
        assertNotNull(captor.getValue().id());
        assertEquals("Servientrega", captor.getValue().name());
    }

    @Test
    void execute_shouldPropagateErrorWhenRepositoryFails() {
        when(repository.saveFranchise(any(Franchise.class)))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(useCase.execute("Servientrega"))
                .expectErrorMessage("DB error")
                .verify();
    }

    // ---------- AddBranchUseCase ----------

    @Test
    void addBranch_shouldSaveBranchWhenFranchiseExists() {
        Franchise franchise = new Franchise("f1", "Burger Corp");
        Branch savedBranch = new Branch("b1", "f1", "Centro");
        when(repository.findFranchiseById("f1")).thenReturn(Mono.just(franchise));
        when(repository.saveBranch(any(Branch.class))).thenReturn(Mono.just(savedBranch));

        StepVerifier.create(addBranchUseCase.execute("f1", "Centro"))
                .expectNext(savedBranch)
                .verifyComplete();

        ArgumentCaptor<Branch> captor = ArgumentCaptor.forClass(Branch.class);
        verify(repository).saveBranch(captor.capture());
        assertEquals("f1", captor.getValue().franchiseId());
        assertEquals("Centro", captor.getValue().name());
    }

    @Test
    void addBranch_shouldFailWhenFranchiseNotFound() {
        when(repository.findFranchiseById("f1")).thenReturn(Mono.empty());

        StepVerifier.create(addBranchUseCase.execute("f1", "Centro"))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(repository, never()).saveBranch(any(Branch.class));
    }

    // ---------- AddProductUseCase ----------

    @Test
    void addProduct_shouldSaveProductWhenBranchExists() {
        Branch branch = new Branch("b1", "f1", "Centro");
        Product savedProduct = new Product("p1", "f1", "b1", "Hamburguesa", 10);
        when(repository.findBranch("f1", "b1")).thenReturn(Mono.just(branch));
        when(repository.saveProduct(any(Product.class))).thenReturn(Mono.just(savedProduct));

        StepVerifier.create(addProductUseCase.execute("f1", "b1", "Hamburguesa", 10))
                .expectNext(savedProduct)
                .verifyComplete();

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(repository).saveProduct(captor.capture());
        assertEquals("b1", captor.getValue().branchId());
        assertEquals(10, captor.getValue().stock());
    }

    @Test
    void addProduct_shouldFailWhenBranchNotFound() {
        when(repository.findBranch("f1", "b1")).thenReturn(Mono.empty());

        StepVerifier.create(addProductUseCase.execute("f1", "b1", "Hamburguesa", 10))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(repository, never()).saveProduct(any(Product.class));
    }

    @Test
    void addProduct_shouldFailWhenStockIsNegative() {
        Branch branch = new Branch("b1", "f1", "Centro");
        when(repository.findBranch("f1", "b1")).thenReturn(Mono.just(branch));

        StepVerifier.create(addProductUseCase.execute("f1", "b1", "Hamburguesa", -5))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(repository, never()).saveProduct(any(Product.class));
    }
}
