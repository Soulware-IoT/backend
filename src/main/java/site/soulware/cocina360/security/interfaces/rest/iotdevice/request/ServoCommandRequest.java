package site.soulware.cocina360.security.interfaces.rest.iotdevice.request;

import jakarta.validation.constraints.NotNull;

public record ServoCommandRequest(
    @NotNull ServoCommand command
) {
    public enum ServoCommand { START, STOP }
}
