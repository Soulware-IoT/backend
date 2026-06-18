package site.soulware.cocina360.organizations.domain.model.valueobject;

import site.soulware.cocina360.shared.domain.model.valueobject.ValueObject;

public record Location(double latitude, double longitude) implements ValueObject {}
