package com.example.module_07.section_05;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.params.converter.ConvertWith;

/**
 * Module 7.5 – a composed annotation: {@code @HumanDuration Duration skew}
 * reads better than {@code @ConvertWith(HumanDurationConverter.class) Duration skew}.
 */
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@ConvertWith(HumanDurationConverter.class)
public @interface HumanDuration {
}
