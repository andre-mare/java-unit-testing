package com.example.module_07.section_01;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import com.example.iban.Iban;
import com.example.iban.IbanGenerator;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * Module 7.1 lab – {@code failureThreshold}: stop repeating once enough repetitions failed.
 * <p>
 * The test claims that a generated IBAN never contains the digit '7', which is
 * wrong about two times in three (ten random digits rarely all avoid a 7). With {@code failureThreshold = 2}, JUnit stops
 * after the second failure and reports the remaining repetitions as skipped:
 * <pre>
 * repetition 1 of 50  ✔ or ✘
 * ...
 * repetition k of 50  ✘   (second failure)
 * repetition k+1..50  ↷ Failure threshold [2] exceeded
 * </pre>
 * When hunting a flaky test, {@code failureThreshold = 1} is enough: one failure
 * proves it is flaky. With parallel execution (Module 9.5) the threshold is only
 * reliable together with {@code @Execution(SAME_THREAD)}, as declared here.
 * <p>
 * FAILS on purpose; tagged {@code expected-failure-tests}. Run it with:
 * <pre>mvn test -Dtest=FlakyHuntDemoTest -DexcludedGroups=none</pre>
 */
@Tag("expected-failure-tests")
@Execution(ExecutionMode.SAME_THREAD)
class FlakyHuntDemoTest {

    @RepeatedTest(value = 50, failureThreshold = 2)
    void generatedIbanNeverContainsASeven(RepetitionInfo repetition) {
        Iban iban = new IbanGenerator(new Random(repetition.getCurrentRepetition())).nextDutchIban();
        System.out.printf("repetition %d: %s (failures so far: %d)%n",
            repetition.getCurrentRepetition(), iban, repetition.getFailureCount());

        assertTrue(iban.bban().indexOf('7') < 0, () -> iban + " contains a 7");
    }
}
