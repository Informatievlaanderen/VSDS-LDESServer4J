package be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.pagenumber.NumericPageNumber;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.pagenumber.PageNumber;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.pagenumber.UuidPageNumber;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class PartialUrlTest {

	@ParameterizedTest
	@ArgumentsSource(PartialUrlProvider.class)
	void test_FromUrl(String url, PartialUrl expected) {
		final PartialUrl partialUrl = PartialUrl.fromUrl(url);

		assertThat(partialUrl)
				.usingRecursiveComparison()
				.isEqualTo(expected);
	}

	@ParameterizedTest
	@ArgumentsSource(PartialUrlProvider.class)
	void test_asString(String expectedUrl, PartialUrl partialUrl) {
		assertThat(partialUrl.asString()).isEqualTo(expectedUrl);
	}

	@ParameterizedTest
	@ArgumentsSource(ChildPartialUrlProvider.class)
	void test_CreateChild(PartialUrl partialUrl, PartialUrl expectedChild) {
		assertThat(partialUrl.createChild())
				.usingRecursiveComparison()
				.isEqualTo(expectedChild);
	}

	@ParameterizedTest
	@ArgumentsSource(NumberedChildPartialUrlProvider.class)
	void test_CreatedNumberedChild(String partialUrl, Class<? extends PageNumber> expectedInstanceClass) {
		final PartialUrl child = PartialUrl.fromUrl(partialUrl).createChild();

		assertThat(child)
				.extracting("pageNumber")
				.isInstanceOf(expectedInstanceClass);
	}

	static class PartialUrlProvider implements ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(
					Arguments.of("/event-stream/paged", new PartialUrl("event-stream/paged", "", null)),
					Arguments.of("/event-stream/by-loc?tile=15/142/123", new PartialUrl("event-stream/by-loc", "tile=15/142/123", null)),
					Arguments.of("/event-stream/by-time?year=2024&month=06&day=28", new PartialUrl("event-stream/by-time", "year=2024&month=06&day=28", null)),
					Arguments.of("/event-stream/paged?pageNumber=2", new PartialUrl("event-stream/paged", "", new NumericPageNumber(2))),
					Arguments.of("/event-stream/by-time?year=2024&month=06&day=28&pageNumber=2", new PartialUrl("event-stream/by-time", "year=2024&month=06&day=28", new NumericPageNumber(2))),
					Arguments.of("/event-stream/by-time?year=2024&month=06&day=28&pageNumber=26929514-237c-11ed-861d-0242ac120002", new PartialUrl("event-stream/by-time", "year=2024&month=06&day=28", UuidPageNumber.fromString("26929514-237c-11ed-861d-0242ac120002")))
			);
		}
	}

	static class ChildPartialUrlProvider implements ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(
					Arguments.of(new PartialUrl("event-stream/paged", "", null), new PartialUrl("event-stream/paged", "", new NumericPageNumber(1))),
					Arguments.of(new PartialUrl("event-stream/by-loc", "tile=15/142/123", null), new PartialUrl("event-stream/by-loc", "tile=15/142/123", new NumericPageNumber(1))),
					Arguments.of(new PartialUrl("event-stream/by-time", "year=2024&month=06&day=28", null), new PartialUrl("event-stream/by-time", "year=2024&month=06&day=28", new NumericPageNumber(1))),
					Arguments.of(new PartialUrl("event-stream/paged", "", new NumericPageNumber(2)), new PartialUrl("event-stream/paged", "", new NumericPageNumber(3))),
					Arguments.of(new PartialUrl("event-stream/by-time", "year=2024&month=06&day=28", new NumericPageNumber(2)), new PartialUrl("event-stream/by-time", "year=2024&month=06&day=28", new NumericPageNumber(3)))
			);
		}
	}

	static class NumberedChildPartialUrlProvider implements ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(
					Arguments.of("/event-stream/by-time?year=2024&month=06&day=28&pageNumber=2", NumericPageNumber.class),
					Arguments.of("/event-stream/by-time?year=2024&month=06&day=28&pageNumber=26929514-237c-11ed-861d-0242ac120002", UuidPageNumber.class)
			);
		}
	}
}