package com.trujillo.franchise_api;

import com.trujillo.franchise_api.application.usecase.CreateFranchiseUseCase;
import com.trujillo.franchise_api.domain.model.Franchise;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateFranchiseUseCaseTest {

    @Mock
    private FranchiseRepository repository;

    @InjectMocks
    private CreateFranchiseUseCase useCase;

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
}
