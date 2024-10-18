package be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.pagenumber;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class UuidPageNumberTest {
	private static final String UUID = "26929514-237c-11ed-861d-0242ac120002";
	private static final UuidPageNumber PAGE_NUMBER = UuidPageNumber.fromString(UUID);

	@Test
	void testEquality() {
		final UuidPageNumber other = UuidPageNumber.fromString(UUID);

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
				UuidPageNumber.fromString("fe15639d-526a-42e2-8d70-cbc239d7156c"),
				UUID,
				new NumericPageNumber(1),
				null
		);
	}
}