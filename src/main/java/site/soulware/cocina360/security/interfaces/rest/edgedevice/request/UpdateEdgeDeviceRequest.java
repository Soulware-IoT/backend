package site.soulware.cocina360.security.interfaces.rest.edgedevice.request;

import io.swagger.v3.oas.annotations.media.Schema;
import site.soulware.cocina360.security.domain.model.command.UpdateEdgeDeviceCommand;

import java.util.UUID;

/**
 * Partial update of a claimed edge device. Every field is optional — omit a field to leave
 * it unchanged. The edge device must already be claimed; these operations are not valid
 * while it is still {@code PROVISIONED}.
 *
 * @param name new display name; omit to leave unchanged.
 * @param status {@code ACTIVE} or {@code INACTIVE} to (de)activate; omit to leave unchanged.
 */
public record UpdateEdgeDeviceRequest(
    @Schema(description = "Optional. New display name; omit to leave unchanged.")
    String name,
    @Schema(description = "Optional. ACTIVE or INACTIVE to (de)activate; omit to leave unchanged.")
    Activation status
) {

    public enum Activation { ACTIVE, INACTIVE }

    public UpdateEdgeDeviceCommand toCommand(UUID edgeDeviceId, UUID requesterId) {
        Boolean activate = this.status == null ? null : this.status == Activation.ACTIVE;
        return new UpdateEdgeDeviceCommand(edgeDeviceId, this.name, activate, requesterId);
    }
}
