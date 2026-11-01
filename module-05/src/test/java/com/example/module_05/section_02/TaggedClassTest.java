package com.example.module_05.section_02;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Set;

import com.example.config.AppConfig;
import com.example.module_05.support.LongRunning;
import com.example.module_05.support.Security;
import com.example.module_05.support.Smoke;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Module 5.2 – {@code @Tag} on a class and on methods, and composed tag annotations.
 * <p>
 * A test's tags are its own tags plus those of its class (class-level tags are
 * inherited; method-level tags are not inherited by overriding methods). Each
 * test checks its full tag set via {@link TestInfo}.
 * <p>
 * Tag syntax: a tag is trimmed and must not be blank or contain whitespace,
 * ISO control characters, or any of {@code , ( ) & | !}. Invalid tags are
 * reported as warnings and ignored.
 */
@Tag("config")
class TaggedClassTest {

    private final AppConfig config = new AppConfig(Map.of("app.env", "staging")::get, key -> null);

    @Test
    void untaggedMethodGetsTheClassTag(TestInfo info) {
        assertEquals(Set.of("config"), info.getTags());
        assertEquals("staging", config.environmentName());
    }

    @Test
    @Tag("fast")
    void methodTagIsAddedToTheClassTag(TestInfo info) {
        assertEquals(Set.of("config", "fast"), info.getTags());
    }

    @Test
    @Tag("fast")
    @Tag("security") // @Tag is repeatable
    void severalTags(TestInfo info) {
        assertEquals(Set.of("config", "fast", "security"), info.getTags());
    }

    @Test
    @Smoke
    @Security
    void composedTagAnnotations(TestInfo info) {
        assertEquals(Set.of("config", "smoke", "security"), info.getTags());
    }

    @LongRunning // @Test + @Tag("slow") + @Timeout(5 s)
    void composedTestTagAndTimeout(TestInfo info) throws InterruptedException {
        assertTrue(info.getTags().contains("slow"));
        Thread.sleep(300);
    }
}
