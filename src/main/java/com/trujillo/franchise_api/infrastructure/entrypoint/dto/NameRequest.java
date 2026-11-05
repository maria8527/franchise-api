package com.trujillo.franchise_api.infrastructure.entrypoint.dto;

import jakarta.validation.constraints.NotBlank;

public record NameRequest(@NotBlank String name) {
}
