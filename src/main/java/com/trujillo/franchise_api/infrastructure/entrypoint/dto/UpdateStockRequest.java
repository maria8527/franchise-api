package com.trujillo.franchise_api.infrastructure.entrypoint.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateStockRequest(@NotNull @PositiveOrZero Integer stock) {
}
