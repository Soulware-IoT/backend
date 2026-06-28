package site.soulware.cocina360.organizations.domain.model.valueobject;

import org.junit.jupiter.api.Test;
import site.soulware.cocina360.organizations.domain.model.exception.PermissionGrantTooHighException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static site.soulware.cocina360.organizations.domain.model.valueobject.PermissionLevel.ADMIN;
import static site.soulware.cocina360.organizations.domain.model.valueobject.PermissionLevel.ASSIGNEE;
import static site.soulware.cocina360.organizations.domain.model.valueobject.PermissionLevel.LIEUTENANT;
import static site.soulware.cocina360.organizations.domain.model.valueobject.PermissionLevel.NONE;

class OrganizationMemberPermissionsTest {

    @Test
    void lieutenantCanAssignLevelsBelowOwn() {
        OrganizationMemberPermissions result =
                OrganizationMemberPermissions.assignableBy(LIEUTENANT, ASSIGNEE, NONE, ASSIGNEE);

        assertThat(result).isEqualTo(new OrganizationMemberPermissions(ASSIGNEE, NONE, ASSIGNEE));
    }

    @Test
    void lieutenantCannotAssignLieutenant() {
        assertThatThrownBy(() -> OrganizationMemberPermissions.assignableBy(LIEUTENANT, LIEUTENANT, NONE, NONE))
                .isInstanceOf(PermissionGrantTooHighException.class);
    }

    @Test
    void lieutenantCannotAssignAdmin() {
        assertThatThrownBy(() -> OrganizationMemberPermissions.assignableBy(LIEUTENANT, NONE, ADMIN, NONE))
                .isInstanceOf(PermissionGrantTooHighException.class);
    }

    @Test
    void adminCanAssignLieutenant() {
        OrganizationMemberPermissions result =
                OrganizationMemberPermissions.assignableBy(ADMIN, LIEUTENANT, ASSIGNEE, NONE);

        assertThat(result).isEqualTo(new OrganizationMemberPermissions(LIEUTENANT, ASSIGNEE, NONE));
    }

    @Test
    void nobodyCanAssignAdminSinceNothingOutranksIt() {
        assertThat(catchThrowable(() -> OrganizationMemberPermissions.assignableBy(ADMIN, ADMIN, NONE, NONE)))
                .isInstanceOf(PermissionGrantTooHighException.class);
    }

    @Test
    void theViolatingAreaIsRejectedRegardlessOfPosition() {
        // internalControl (3rd arg) is the offending one — still rejected.
        assertThatThrownBy(() -> OrganizationMemberPermissions.assignableBy(LIEUTENANT, NONE, NONE, LIEUTENANT))
                .isInstanceOf(PermissionGrantTooHighException.class);
    }
}
