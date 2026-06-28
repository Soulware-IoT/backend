package site.soulware.cocina360.organizations.domain.model.valueobject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static site.soulware.cocina360.organizations.domain.model.valueobject.PermissionLevel.ADMIN;
import static site.soulware.cocina360.organizations.domain.model.valueobject.PermissionLevel.ASSIGNEE;
import static site.soulware.cocina360.organizations.domain.model.valueobject.PermissionLevel.LIEUTENANT;
import static site.soulware.cocina360.organizations.domain.model.valueobject.PermissionLevel.NONE;

class PermissionLevelTest {

    @Test
    void rankOrdersAscendingFromNoneToAdmin() {
        assertThat(NONE.rank()).isLessThan(ASSIGNEE.rank());
        assertThat(ASSIGNEE.rank()).isLessThan(LIEUTENANT.rank());
        assertThat(LIEUTENANT.rank()).isLessThan(ADMIN.rank());
    }

    @ParameterizedTest(name = "{0} isBelow {1} -> {2}")
    @CsvSource({
        "NONE, ASSIGNEE, true",
        "ASSIGNEE, LIEUTENANT, true",
        "ASSIGNEE, ADMIN, true",
        "LIEUTENANT, ADMIN, true",
        "LIEUTENANT, LIEUTENANT, false",
        "ADMIN, ADMIN, false",
        "ADMIN, LIEUTENANT, false",
        "ASSIGNEE, ASSIGNEE, false",
        "ASSIGNEE, NONE, false"
    })
    void isBelowIsStrictlyLess(PermissionLevel level, PermissionLevel other, boolean expected) {
        assertThat(level.isBelow(other)).isEqualTo(expected);
    }
}
