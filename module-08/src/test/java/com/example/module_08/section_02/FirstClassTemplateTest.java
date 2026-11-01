package com.example.module_08.section_02;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.example.scope.ScopeSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.ClassTemplate;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ClassTemplateInvocationContext;
import org.junit.jupiter.api.extension.ClassTemplateInvocationContextProvider;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedClass;

/**
 * Module 8.2 – the smallest class template, with its lifecycle recorded.
 * <p>
 * The provider is a static nested class and sets a field through a
 * {@code TestInstancePostProcessor}, as in the user guide. Two invocations, two
 * tests each: four tests in the report. {@code @BeforeAll} and {@code @AfterAll}
 * run once around all invocations, the same as for {@code @ParameterizedClass},
 * which is the built-in class template (the last test proves that).
 */
@ClassTemplate
@ExtendWith(FirstClassTemplateTest.ScopeProvider.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
@Execution(ExecutionMode.SAME_THREAD)
class FirstClassTemplateTest {

    private static final List<String> log = new ArrayList<>();

    String requestedScope;

    @BeforeAll
    static void beforeAll() {
        log.add("beforeAll");
    }

    @Test
    void a_parses() {
        log.add("a " + requestedScope);
        assertTrue(ScopeSet.parse(requestedScope).contains("openid"));
    }

    @Test
    void b_parameterizedClassIsAClassTemplate() {
        log.add("b " + requestedScope);
        assertTrue(ParameterizedClass.class.isAnnotationPresent(ClassTemplate.class));
    }

    @AfterAll
    static void afterAll() {
        assertEquals(List.of("beforeAll", "a openid", "b openid", "a openid accounts", "b openid accounts"), log);
    }

    static class ScopeProvider implements ClassTemplateInvocationContextProvider {

        @Override
        public boolean supportsClassTemplate(ExtensionContext context) {
            return true;
        }

        @Override
        public Stream<ClassTemplateInvocationContext> provideClassTemplateInvocationContexts(ExtensionContext context) {
            return Stream.of("openid", "openid accounts").map(ScopeProvider::invocation);
        }

        private static ClassTemplateInvocationContext invocation(String scope) {
            return new ClassTemplateInvocationContext() {

                @Override
                public String getDisplayName(int invocationIndex) {
                    return "scope=" + scope;
                }

                @Override
                public List<Extension> getAdditionalExtensions() {
                    return List.of((TestInstancePostProcessor) (instance, ctx) ->
                        ((FirstClassTemplateTest) instance).requestedScope = scope);
                }
            };
        }
    }
}
