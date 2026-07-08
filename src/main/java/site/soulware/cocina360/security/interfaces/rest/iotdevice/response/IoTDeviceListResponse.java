package site.soulware.cocina360.security.interfaces.rest.iotdevice.response;

import java.util.List;

public record IoTDeviceListResponse(
    List<IoTDeviceResponse> devices,
    Quota quota
) {
    public record Quota(long used, int limit) {}
}
