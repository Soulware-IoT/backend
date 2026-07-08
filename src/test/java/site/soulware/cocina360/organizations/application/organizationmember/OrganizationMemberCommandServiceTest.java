package site.soulware.cocina360.organizations.application.organizationmember;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import site.soulware.cocina360.organizations.domain.model.aggregate.OrganizationMember;
import site.soulware.cocina360.organizations.domain.model.command.UpdateMemberPermissionsCommand;
import site.soulware.cocina360.organizations.domain.model.exception.InsufficientPermissionException;
import site.soulware.cocina360.organizations.domain.model.exception.PermissionGrantTooHighException;
import site.soulware.cocina360.organizations.domain.model.valueobject.OrganizationMemberId;
import site.soulware.cocina360.organizations.domain.model.valueobject.OrganizationMemberPermissions;
import site.soulware.cocina360.organizations.domain.model.valueobject.PermissionLevel;
import site.soulware.cocina360.organizations.domain.repository.OrganizationMemberRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrganizationMemberCommandServiceTest {

    private final UUID organizationId = UUID.randomUUID();
    private final UUID memberId = UUID.randomUUID();
    private final UUID requesterId = UUID.randomUUID();

    private final OrganizationMemberRepository repository = mock(OrganizationMemberRepository.class);
    private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    private final OrganizationMemberCommandService service =
            new OrganizationMemberCommandService(this.repository, this.eventPublisher);

    private OrganizationMember memberWithOrganizationsLevel(UUID profileId, PermissionLevel organizationsLevel) {
        return OrganizationMember.rehydrate(
                OrganizationMemberId.of(UUID.randomUUID()),
                ProfileId.of(profileId),
                OrganizationId.of(this.organizationId),
                null,
                Instant.now(),
                new OrganizationMemberPermissions(PermissionLevel.NONE, organizationsLevel, PermissionLevel.NONE));
    }

    private void givenActorLevel(PermissionLevel actorOrganizationsLevel) {
        OrganizationMember target = this.memberWithOrganizationsLevel(UUID.randomUUID(), PermissionLevel.NONE);
        OrganizationMember actor = this.memberWithOrganizationsLevel(this.requesterId, actorOrganizationsLevel);
        when(this.repository.findById(any(OrganizationMemberId.class))).thenReturn(Optional.of(target));
        when(this.repository.findByOrganizationIdAndProfileId(any(OrganizationId.class), any(ProfileId.class)))
                .thenReturn(Optional.of(actor));
    }

    private UpdateMemberPermissionsCommand command(PermissionLevel assignedSecurity) {
        return new UpdateMemberPermissionsCommand(this.organizationId, this.memberId, this.requesterId,
                assignedSecurity, PermissionLevel.NONE, PermissionLevel.NONE);
    }

    @Test
    void adminMayAssignLieutenantAndPersists() {
        this.givenActorLevel(PermissionLevel.ADMIN);

        this.service.handle(this.command(PermissionLevel.LIEUTENANT));

        verify(this.repository).save(any(OrganizationMember.class));
    }

    @Test
    void lieutenantAssigningLieutenantIsRejectedAndNotPersisted() {
        this.givenActorLevel(PermissionLevel.LIEUTENANT);

        assertThatThrownBy(() -> this.service.handle(this.command(PermissionLevel.LIEUTENANT)))
                .isInstanceOf(PermissionGrantTooHighException.class);

        verify(this.repository, never()).save(any());
    }

    @Test
    void requesterThatIsNotAMemberIsForbidden() {
        OrganizationMember target = this.memberWithOrganizationsLevel(UUID.randomUUID(), PermissionLevel.NONE);
        when(this.repository.findById(any(OrganizationMemberId.class))).thenReturn(Optional.of(target));
        when(this.repository.findByOrganizationIdAndProfileId(any(OrganizationId.class), any(ProfileId.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.service.handle(this.command(PermissionLevel.ASSIGNEE)))
                .isInstanceOf(InsufficientPermissionException.class);

        verify(this.repository, never()).save(any());
    }
}
