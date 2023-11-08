package com.kntronov.makespace.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalTime;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("TimeSlot Test")
class TimeSlotTest {

    private static final LocalTime validStart = LocalTime.of(14, 30, 0);
    private static final LocalTime validEnd = LocalTime.of(14, 45, 0);
    private static final Throwable nullStartException = new IllegalArgumentException("start must not be null");
    private static final Throwable nullEndException = new IllegalArgumentException("end must not be null");
    private static final Throwable invalidTimeSlotException = new IllegalArgumentException("slot minutes must be one of [0, 15, 30, 45]");
    private static final Throwable invalidRangeException = new IllegalArgumentException("end must be after start");

    private static Stream<Arguments> provideInvalidTimeSlotValues() {
        return Stream.of(
                Arguments.of(1),
                Arguments.of(14),
                Arguments.of(16),
                Arguments.of(29),
                Arguments.of(31),
                Arguments.of(44),
                Arguments.of(46),
                Arguments.of(59)
        );
    }

    @Test
    @DisplayName("when start is null should fail validation")
    void nullStartTest() {
        assertThatThrownBy(() -> {
            new TimeSlot(null, validEnd);
        }).hasSameClassAs(nullStartException).hasMessage(nullStartException.getMessage());
    }

    @Test
    @DisplayName("when end is null should fail validation")
    void nullEndTest() {
        assertThatThrownBy(() -> {
            new TimeSlot(validStart, null);
        }).hasSameClassAs(nullEndException).hasMessage(nullEndException.getMessage());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidTimeSlotValues")
    @DisplayName("when start has invalid format should fail validation")
    void invalidStartTimeTest(int invalidMinutes) {
        final var invalidStart = LocalTime.of(12, invalidMinutes, 0);
        assertThatThrownBy(() -> {
            new TimeSlot(invalidStart, validEnd);
        }).hasSameClassAs(invalidTimeSlotException).hasMessage(invalidTimeSlotException.getMessage());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidTimeSlotValues")
    @DisplayName("when end has invalid format should fail validation")
    void invalidEndTimeTest(int invalidMinutes) {
        final var invalidEnd = LocalTime.of(12, invalidMinutes, 0);
        assertThatThrownBy(() -> {
            new TimeSlot(validStart, invalidEnd);
        }).hasSameClassAs(invalidTimeSlotException).hasMessage(invalidTimeSlotException.getMessage());
    }

    @Test
    @DisplayName("when start and end are an invalid range should fail validation")
    void invalidRangeTest() {
        assertThatThrownBy(() -> {
            new TimeSlot(validEnd, validStart);
        }).hasSameClassAs(invalidRangeException).hasMessage(invalidRangeException.getMessage());
    }

    @Nested
    @DisplayName("isOverlapping")
    class IsOverlappingTest {

        private static Stream<Arguments> provideOverlappingCases() {
            return Stream.of(
                    Arguments.of(
                            new TimeSlot(
                                    LocalTime.of(10, 0),
                                    LocalTime.of(10, 30)
                            ),
                            new TimeSlot(
                                    LocalTime.of(10, 0),
                                    LocalTime.of(10, 30)
                            ),
                            true
                    ),
                    Arguments.of(
                            new TimeSlot(
                                    LocalTime.of(10, 0),
                                    LocalTime.of(10, 30)
                            ),
                            new TimeSlot(
                                    LocalTime.of(10, 15),
                                    LocalTime.of(11, 0)
                            ),
                            true
                    ),
                    Arguments.of(
                            new TimeSlot(
                                    LocalTime.of(10, 0),
                                    LocalTime.of(10, 30)
                            ),
                            new TimeSlot(
                                    LocalTime.of(9, 0),
                                    LocalTime.of(10, 15)
                            ),
                            true
                    ),
                    Arguments.of(
                            new TimeSlot(
                                    LocalTime.of(10, 0),
                                    LocalTime.of(10, 30)
                            ),
                            new TimeSlot(
                                    LocalTime.of(10, 30),
                                    LocalTime.of(10, 45)
                            ),
                            false
                    ),
                    Arguments.of(
                            new TimeSlot(
                                    LocalTime.of(10, 0),
                                    LocalTime.of(10, 30)
                            ),
                            new TimeSlot(
                                    LocalTime.of(9, 0),
                                    LocalTime.of(10, 0)
                            ),
                            false
                    ),
                    Arguments.of(
                            new TimeSlot(
                                    LocalTime.of(10, 0),
                                    LocalTime.of(10, 30)
                            ),
                            new TimeSlot(
                                    LocalTime.of(11, 0),
                                    LocalTime.of(11, 30)
                            ),
                            false
                    )
            );
        }

        @ParameterizedTest
        @MethodSource("provideOverlappingCases")
        @DisplayName("when timeslots are checked for overlap should return true if overlapping else false")
        void isOverlappingTrueTest(TimeSlot a, TimeSlot b, boolean isOverlapping) {
            assertThat(a.isOverlapping(b)).isEqualTo(isOverlapping);
        }
    }
}
