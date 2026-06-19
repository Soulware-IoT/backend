package site.soulware.cocina360.security.application.edgedevice;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.security.domain.model.exception.EdgeDeviceNotFoundException;
import site.soulware.cocina360.security.domain.model.exception.InvalidEdgeApiKeyException;
import site.soulware.cocina360.security.domain.model.query.AuthenticateEdgeQuery;
import site.soulware.cocina360.security.domain.model.query.GetEdgeDeviceByOrganizationQuery;
import site.soulware.cocina360.security.domain.model.query.GetEdgeDeviceQuery;
import site.soulware.cocina360.security.domain.model.valueobject.ApiKey;
import site.soulware.cocina360.security.domain.model.valueobject.EdgeDeviceId;
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
     * @throws InvalidEdgeApiKeyException if the key is missing/blank or unrecognised (401).
     */
    public EdgeDeviceResult handle(AuthenticateEdgeQuery query) {
        if (query.apiKey() == null || query.apiKey().isBlank()) {
            throw new InvalidEdgeApiKeyException();
        }
        return this.edgeDeviceRepository.findByApiKey(ApiKey.of(query.apiKey()))
                .map(EdgeDeviceResult::from)
                .orElseThrow(InvalidEdgeApiKeyException::new);
    }
}
