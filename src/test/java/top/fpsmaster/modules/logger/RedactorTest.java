package top.fpsmaster.modules.logger;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RedactorTest {
    @Test
    void masksTokenShapes() {
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjMifQ.abcDEF_123-xyz";
        assertEquals("token " + Redactor.MASK + " end", Redactor.redact("token " + jwt + " end"));
        assertEquals("Authorization: Bearer " + Redactor.MASK, Redactor.redact("Authorization: Bearer abcdef0123456789"));
        assertEquals("-Dfpsmaster.auth.token=" + Redactor.MASK + " -Xmx2G",
                Redactor.redact("-Dfpsmaster.auth.token=plainsecret -Xmx2G"));
        assertEquals("FPSMASTER_LAUNCH_TOKEN=" + Redactor.MASK, Redactor.redact("FPSMASTER_LAUNCH_TOKEN=plainsecret"));
        assertEquals("{\"accessToken\":\"" + Redactor.MASK + "\"}", Redactor.redact("{\"accessToken\":\"abc.def\"}"));
        assertEquals("refresh_token=" + Redactor.MASK + "&x=1", Redactor.redact("refresh_token=M.R3_abc&x=1"));
    }

    @Test
    void masksRegisteredSecretsAndLeavesOrdinaryTextAlone() {
        Redactor.registerSecret("opaque-launch-secret-42");
        assertFalse(Redactor.redact("failed with opaque-launch-secret-42 in url").contains("opaque-launch-secret-42"));
        String plain = "Loaded config profile: default (12 modules)";
        assertEquals(plain, Redactor.redact(plain));
        assertTrue(Redactor.redact(new IllegalStateException("bad opaque-launch-secret-42")).contains(Redactor.MASK));
    }
}
