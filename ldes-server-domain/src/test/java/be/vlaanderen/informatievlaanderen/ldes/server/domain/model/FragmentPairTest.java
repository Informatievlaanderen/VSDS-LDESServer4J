package be.vlaanderen.informatievlaanderen.ldes.server.domain.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class FragmentPairTest {

    @ParameterizedTest
    @MethodSource("provideEqualsTestArguments")
    void testEquals(FragmentPair pair1, Object pair2, boolean expectedResult) {
        assertThat(expectedResult).isEqualTo(pair1.equals(pair2));
    }

    private static Stream<Arguments> provideEqualsTestArguments() {
        FragmentPair pair1 = new FragmentPair("key1", "value1");
        FragmentPair pair2 = new FragmentPair("key1", "value1");
        FragmentPair pair3 = new FragmentPair("key2", "value1");
        String otherClass = "Some String";

        return Stream.of(
                Arguments.of(pair1, pair1, true),
                Arguments.of(pair1, pair2, true),
                Arguments.of(pair1, pair3, false),
                Arguments.of(pair1, null, false),
                Arguments.of(pair1, otherClass, false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideHashCodeTestArguments")
    void testHashCode(FragmentPair pair, int expectedHashCode) {
        assertThat(expectedHashCode).isEqualTo(pair.hashCode());
    }

    private static Stream<Arguments> provideHashCodeTestArguments() {
        return Stream.of(
                Arguments.of(new FragmentPair("key1", "value1"), new FragmentPair("key1", "value1").hashCode()),
                Arguments.of(new FragmentPair("key2", "value2"), new FragmentPair("key2", "value2").hashCode())
        );
    }

}