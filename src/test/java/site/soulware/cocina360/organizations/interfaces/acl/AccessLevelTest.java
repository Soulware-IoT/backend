package site.soulware.cocina360.organizations.interfaces.acl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static site.soulware.cocina360.organizations.interfaces.acl.AccessLevel.ADMIN;
import static site.soulware.cocina360.organizations.interfaces.acl.AccessLevel.ASSIGNEE;
import static site.soulware.cocina360.organizations.interfaces.acl.AccessLevel.LIEUTENANT;
import static site.soulware.cocina360.organizations.interfaces.acl.AccessLevel.NONE;

class AccessLevelTest {

    @Test
    void aLevelSatisfiesItselfAndEveryLowerMinimum() {
        assertThat(ADMIN.satisfies(ADMIN)).isTrue();
        assertThat(ADMIN.satisfies(LIEUTENANT)).isTrue();
        assertThat(ADMIN.satisfies(ASSIGNEE)).isTrue();
        assertThat(ADMIN.satisfies(NONE)).isTrue();
    }

    @ParameterizedTest(name = "{0} satisfies min {1} -> {2}")
    @CsvSource({
        "NONE, NONE, true",
        "NONE, ASSIGNEE, false",
        "ASSIGNEE, ASSIGNEE, true",
        "ASSIGNEE, LIEUTENANT, false",
        "LIEUTENANT, ASSIGNEE, true",
        "LIEUTENANT, LIEUTENANT, true",
        "LIEUTENANT, ADMIN, false",
        "ADMIN, LIEUTENANT, true"
    })
    void satisfiesIsRankAtLeastMinimum(AccessLevel level, AccessLevel minimum, boolean expected) {
        assertThat(level.satisfies(minimum)).isEqualTo(expected);
    }
}
