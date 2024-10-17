package be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.pagenumber;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class NumericPageNumberTest {
	private static final int PAGE_NUMBER_VALUE = 3;
	private static final NumericPageNumber PAGE_NUMBER = new NumericPageNumber(PAGE_NUMBER_VALUE);

	@Test
	void testEquality() {
		final NumericPageNumber other = new NumericPageNumber(PAGE_NUMBER_VALUE);

		assertThat(PAGE_NUMBER)
				.isEqualTo(other)
				.isEqualTo(PAGE_NUMBER)
				.hasSameHashCodeAs(other);
		assertThat(other)
				.isEqualTo(PAGE_NUMBER);
	}

	@ParameterizedTest
	@MethodSource
	void testInequality(Object other) {
		assertThat(other).isNotEqualTo(PAGE_NUMBER);

		if(other != null) {
			assertThat(other).doesNotHaveSameHashCodeAs(PAGE_NUMBER);
		}
	}

	static Stream<Object> testInequality() {
		return Stream.of(
				new NumericPageNumber(10),
				PAGE_NUMBER_VALUE,
				new UuidPageNumber(),
				null
		);
	}
}