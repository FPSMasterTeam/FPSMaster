package top.fpsmaster.modules.account;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AccountManagerMergeTest {
    @Test
    void legacyAccountsAreMergedIntoExistingTarget() {
        List<AccountManager.Account> target = new ArrayList<AccountManager.Account>();
        String selected = AccountManager.parse("{\"selected\":\"Alpha\",\"accounts\":["
                + "{\"name\":\"Alpha\",\"type\":\"offline\"},"
                + "{\"name\":\"MsOne\",\"uuid\":\"0123456789abcdef0123456789abcdef\",\"type\":\"microsoft\","
                + "\"refreshToken\":\"keep\"}]}", target);
        assertEquals("Alpha", selected);

        List<AccountManager.Account> legacy = new ArrayList<AccountManager.Account>();
        String legacySelected = AccountManager.parse("{\"selected\":\"MsTwo\",\"accounts\":["
                + "{\"name\":\"alpha\",\"type\":\"offline\"},"
                + "{\"name\":\"MsOne\",\"uuid\":\"01234567-89ab-cdef-0123-456789abcdef\",\"type\":\"microsoft\","
                + "\"refreshToken\":\"stale\"},"
                + "{\"name\":\"MsTwo\",\"uuid\":\"fedcba9876543210fedcba9876543210\",\"type\":\"microsoft\","
                + "\"refreshToken\":\"other\"},"
                + "{\"name\":\"Beta\"}]}", legacy);
        assertEquals("MsTwo", legacySelected);

        AccountManager.merge(target, legacy);

        assertEquals(4, target.size());
        assertEquals("keep", target.get(1).refreshToken);
        assertEquals("MsTwo", target.get(2).name);
        assertEquals("other", target.get(2).refreshToken);
        assertEquals("Beta", target.get(3).name);
        assertEquals(AccountManager.TYPE_OFFLINE, target.get(3).type);
    }

    @Test
    void bareArrayHasNoSelection() {
        List<AccountManager.Account> out = new ArrayList<AccountManager.Account>();
        assertNull(AccountManager.parse("[{\"name\":\"Gamma\"}]", out));
        assertEquals(1, out.size());
    }
}
