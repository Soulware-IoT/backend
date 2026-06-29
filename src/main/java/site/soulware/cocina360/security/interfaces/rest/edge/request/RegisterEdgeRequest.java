package site.soulware.cocina360.security.interfaces.rest.edge.request;

import jakarta.validation.constraints.NotBlank;

public record RegisterEdgeRequest(@NotBlank String ip) {}
