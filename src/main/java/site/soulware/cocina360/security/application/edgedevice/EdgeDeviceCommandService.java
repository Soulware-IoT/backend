package site.soulware.cocina360.security.application.edgedevice;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.security.domain.model.aggregate.EdgeDevice;
import site.soulware.cocina360.security.domain.model.command.ClaimEdgeDeviceCommand;
import site.soulware.cocina360.security.domain.model.exception.EdgeDeviceNotFoundException;
import site.soulware.cocina360.security.domain.model.exception.OrganizationAlreadyHasEdgeDeviceException;
import site.soulware.cocina360.security.domain.model.valueobject.ApiKey;
import site.soulware.cocina360.security.domain.model.valueobject.EdgeDeviceCode;
import site.soulware.cocina360.security.domain.model.valueobject.EdgeDeviceId;
import site.soulware.cocina360.security.domain.repository.EdgeDeviceRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

@Service
@Transactional
public class EdgeDeviceCommandService {

    private final EdgeDeviceRepository edgeDeviceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public EdgeDeviceCommandService(
        EdgeDeviceRepository edgeDeviceRepository,
        ApplicationEventPublisher eventPublisher
    ) {
        this.edgeDeviceRepository = edgeDeviceRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Factory step: mint a new unassigned edge device with a generated, unique code +
     * apiKey. Returns the credentials so they can be written into the edge's configuration.
     */
    public ProvisionedEdgeDeviceResult provision() {
        EdgeDeviceCode code = this.uniqueCode();
        EdgeDevice edgeDevice = EdgeDevice.provision(EdgeDeviceId.generate(), code, ApiKey.generate());

        this.edgeDeviceRepository.save(edgeDevice);
        edgeDevice.pullDomainEvents().forEach(this.eventPublisher::publishEvent);

        return ProvisionedEdgeDeviceResult.from(edgeDevice);
    }

    /**
     * Claim a previously provisioned edge device (by code) into an organization,
     * enforcing the 1:1 organization invariant.
     *
     * @throws EdgeDeviceNotFoundException if no edge device exists with that code.
     * @throws OrganizationAlreadyHasEdgeDeviceException if the organization already has one.
     */
    public EdgeDeviceId handle(ClaimEdgeDeviceCommand command) {
        OrganizationId organizationId = OrganizationId.of(command.organizationId());
        if (this.edgeDeviceRepository.existsByOrganizationId(organizationId)) {
            throw new OrganizationAlreadyHasEdgeDeviceException(organizationId.value());
        }

        EdgeDeviceCode code = EdgeDeviceCode.of(command.code());
        EdgeDevice edgeDevice = this.edgeDeviceRepository.findByCode(code)
                .orElseThrow(() -> EdgeDeviceNotFoundException.byCode(code.value()));

        edgeDevice.claim(organizationId, command.name(), ProfileId.of(command.requesterId()));

        this.edgeDeviceRepository.save(edgeDevice);
        edgeDevice.pullDomainEvents().forEach(this.eventPublisher::publishEvent);

        return edgeDevice.getId();
    }

    private EdgeDeviceCode uniqueCode() {
        EdgeDeviceCode code = EdgeDeviceCode.generate();
        while (this.edgeDeviceRepository.existsByCode(code)) {
            code = EdgeDeviceCode.generate();
        }
        return code;
    }
}
