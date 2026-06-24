package site.soulware.cocina360.organizations.domain.model.valueobject;

import site.soulware.cocina360.shared.domain.model.valueobject.ValueObject;

public record OrganizationAddress(
    String lineOne,
    String lineTwo,
    String reference
) implements ValueObject {}
