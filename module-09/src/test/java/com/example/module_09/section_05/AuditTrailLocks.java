package com.example.module_09.section_05;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import com.example.audit.AuditTrail;

import org.junit.jupiter.api.parallel.ResourceAccessMode;
import org.junit.jupiter.api.parallel.ResourceLocksProvider;

/**
 * Module 9.5 – a {@link ResourceLocksProvider}: locks decided in code, not per annotation.
 * <p>
 * Convention: a test method whose name starts with {@code reads} gets a shared
 * {@code READ} lock on the audit trail, every other method an exclusive
 * {@code READ_WRITE} lock. Registered with {@code @ResourceLock(providers = AuditTrailLocks.class)}.
 * A provider needs a no-argument constructor; it can also add locks for a whole
 * class ({@code provideForClass}) or a nested class ({@code provideForNestedClass}).
 */
public class AuditTrailLocks implements ResourceLocksProvider {

    @Override
    public Set<Lock> provideForMethod(List<Class<?>> enclosingInstanceTypes, Class<?> testClass, Method testMethod) {
        ResourceAccessMode mode = testMethod.getName().startsWith("reads") ? ResourceAccessMode.READ : ResourceAccessMode.READ_WRITE;
        return Set.of(new Lock(AuditTrail.LOCK, mode));
    }
}
