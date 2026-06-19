package site.soulware.cocina360.security.application.edgegateway;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.security.domain.model.exception.EdgeGatewayNotFoundException;
import site.soulware.cocina360.security.domain.model.query.GetEdgeGatewayByOrganizationQuery;
import site.soulware.cocina360.security.domain.model.query.GetEdgeGatewayQuery;
import site.soulware.cocina360.security.domain.model.valueobject.EdgeGatewayId;
import site.soulware.cocina360.security.domain.repository.EdgeGatewayRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;

@Service
@Transactional(readOnly = true)
public class EdgeGatewayQueryService {

    private final EdgeGatewayRepository edgeGatewayRepository;

    public EdgeGatewayQueryService(EdgeGatewayRepository edgeGatewayRepository) {
        this.edgeGatewayRepository = edgeGatewayRepository;
    }

    public EdgeGatewayResult handle(GetEdgeGatewayQuery query) {
        return this.edgeGatewayRepository.findById(EdgeGatewayId.of(query.edgeGatewayId()))
                .map(EdgeGatewayResult::from)
                .orElseThrow(() -> EdgeGatewayNotFoundException.byId(query.edgeGatewayId()));
    }

    public EdgeGatewayResult handle(GetEdgeGatewayByOrganizationQuery query) {
        return this.edgeGatewayRepository.findByOrganizationId(OrganizationId.of(query.organizationId()))
                .map(EdgeGatewayResult::from)
                .orElseThrow(() -> EdgeGatewayNotFoundException.byOrganizationId(query.organizationId()));
    }
}
