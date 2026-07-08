package site.soulware.cocina360.organizations.interfaces.acl;

import org.junit.jupiter.api.Test;
import site.soulware.cocina360.organizations.application.authorization.AuthorizationQueryService;
import site.soulware.cocina360.organizations.application.authorization.MemberLevels;
import site.soulware.cocina360.organizations.domain.model.exception.InsufficientPermissionException;
import site.soulware.cocina360.organizations.domain.model.valueobject.PermissionLevel;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthorizationApiImplTest {

    private final UUID orgId = UUID.randomUUID();
    private final UUID profileId = UUID.randomUUID();
    private final AuthorizationQueryService queryService = mock(AuthorizationQueryService.class);
    private final AuthorizationApiImpl api = new AuthorizationApiImpl(this.queryService);

    private void stubLevels(PermissionLevel security, PermissionLevel organizations, PermissionLevel internalControl) {
        when(this.queryService.findLevels(this.orgId, this.profileId))
                .thenReturn(Optional.of(new MemberLevels(security, organizations, internalControl)));
    }

    @Test
    void currentLevelSelectsTheRequestedAreaColumn() {
        this.stubLevels(PermissionLevel.ADMIN, PermissionLevel.NONE, PermissionLevel.ASSIGNEE);

        assertThat(this.api.currentLevel(this.orgId, this.profileId, PermissionArea.SECURITY)).isEqualTo(AccessLevel.ADMIN);
        assertThat(this.api.currentLevel(this.orgId, this.profileId, PermissionArea.ORGANIZATIONS)).isEqualTo(AccessLevel.NONE);
        assertThat(this.api.currentLevel(this.orgId, this.profileId, PermissionArea.INTERNAL_CONTROL)).isEqualTo(AccessLevel.ASSIGNEE);
    }

    @Test
    void currentLevelIsNoneWhenProfileIsNotAMember() {
        when(this.queryService.findLevels(this.orgId, this.profileId)).thenReturn(Optional.empty());

        assertThat(this.api.currentLevel(this.orgId, this.profileId, PermissionArea.SECURITY)).isEqualTo(AccessLevel.NONE);
    }

    @Test
    void requirePermissionPassesWhenLevelSatisfiesMinimum() {
        this.stubLevels(PermissionLevel.LIEUTENANT, PermissionLevel.NONE, PermissionLevel.NONE);

        assertThatCode(() -> this.api.requirePermission(this.orgId, this.profileId, PermissionArea.SECURITY, AccessLevel.ASSIGNEE))
                .doesNotThrowAnyException();
    }

    @Test
    void requirePermissionThrowsWhenLevelBelowMinimum() {
        this.stubLevels(PermissionLevel.ASSIGNEE, PermissionLevel.NONE, PermissionLevel.NONE);

        assertThatThrownBy(() -> this.api.requirePermission(this.orgId, this.profileId, PermissionArea.SECURITY, AccessLevel.LIEUTENANT))
                .isInstanceOf(InsufficientPermissionException.class);
    }

    @Test
    void requirePermissionThrowsWhenProfileIsNotAMember() {
        when(this.queryService.findLevels(this.orgId, this.profileId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.api.requirePermission(this.orgId, this.profileId, PermissionArea.ORGANIZATIONS, AccessLevel.ASSIGNEE))
                .isInstanceOf(InsufficientPermissionException.class);
    }
}
