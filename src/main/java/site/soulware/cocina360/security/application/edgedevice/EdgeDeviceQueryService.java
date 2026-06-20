package site.soulware.cocina360.security.application.edgedevice;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.security.domain.model.exception.EdgeDeviceNotFoundException;
import site.soulware.cocina360.security.domain.model.exception.InvalidEdgeApiKeyException;
import site.soulware.cocina360.security.domain.model.query.AuthenticateEdgeQuery;
import site.soulware.cocina360.security.domain.model.query.GetEdgeDeviceByOrganizationQuery;
import site.soulware.cocina360.security.domain.model.query.GetEdgeDeviceQuery;
import site.soulware.cocina360.security.domain.model.aggregate.EdgeDevice;
import site.soulware.cocina360.security.domain.model.valueobject.ApiKey;
import site.soulware.cocina360.security.domain.model.valueobject.EdgeDeviceId;
import site.soulware.cocina360.security.domain.model.valueobject.EdgeDeviceStatus;
import site.soulware.cocina360.security.domain.repository.EdgeDeviceRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;

@Service
@Transactional(readOnly = true)
public class EdgeDeviceQueryService {

    private final EdgeDeviceRepository edgeDeviceRepository;

    public EdgeDeviceQueryService(EdgeDeviceRepository edgeDeviceRepository) {
        this.edgeDeviceRepository = edgeDeviceRepository;
    }

    public EdgeDeviceResult handle(GetEdgeDeviceQuery query) {
        return this.edgeDeviceRepository.findById(EdgeDeviceId.of(query.edgeDeviceId()))
                .map(EdgeDeviceResult::from)
                .orElseThrow(() -> EdgeDeviceNotFoundException.byId(query.edgeDeviceId()));
    }

    public EdgeDeviceResult handle(GetEdgeDeviceByOrganizationQuery query) {
        return this.edgeDeviceRepository.findByOrganizationId(OrganizationId.of(query.organizationId()))
                .map(EdgeDeviceResult::from)
                .orElseThrow(() -> EdgeDeviceNotFoundException.byOrganizationId(query.organizationId()));
    }

    /**
     * Authenticate an inbound edge request by its API key and resolve the edge device
     * (and thus its organization). The single point edge-facing endpoints use to link a
     * caller to its org.
     *
     * Only {@code ACTIVE} edges authenticate: a provisioned-but-unclaimed or deactivated
     * edge holds a valid key but has no organization to act for, so it is rejected here.
     *
     * @throws InvalidEdgeApiKeyException if the key is missing/blank, unrecognised, or the
     *         resolved edge is not {@code ACTIVE} (401).
     */
    public EdgeDeviceResult handle(AuthenticateEdgeQuery query) {
        if (query.apiKey() == null || query.apiKey().isBlank()) {
            throw new InvalidEdgeApiKeyException();
        }
        EdgeDevice edge = this.edgeDeviceRepository.findByApiKey(ApiKey.of(query.apiKey()))
                .orElseThrow(InvalidEdgeApiKeyException::new);
        if (edge.getStatus() != EdgeDeviceStatus.ACTIVE) {
            throw new InvalidEdgeApiKeyException();
        }
        return EdgeDeviceResult.from(edge);
    }
}
