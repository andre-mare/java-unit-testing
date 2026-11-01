package com.example.module_09.section_01;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.io.TempDir;

/**
 * Module 9.1 – composed annotation: {@code @JimfsTempDir Path dir} instead of
 * {@code @TempDir(factory = JimfsTempDirFactory.class) Path dir}.
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@TempDir(factory = JimfsTempDirFactory.class)
public @interface JimfsTempDir {
}
