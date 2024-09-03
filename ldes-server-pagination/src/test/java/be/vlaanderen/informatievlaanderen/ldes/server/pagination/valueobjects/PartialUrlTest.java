package be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects;

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

	static class PartialUrlProvider implements ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
			return Stream.of(
					Arguments.of("/event-stream/paged", new PartialUrl("event-stream/paged", "", null)),
					Arguments.of("/event-stream/by-loc?tile=15/142/123", new PartialUrl("event-stream/by-loc", "tile=15/142/123", null)),
					Arguments.of("/event-stream/by-time?year=2024&month=06&day=28", new PartialUrl("event-stream/by-time", "year=2024&month=06&day=28", null)),
					Arguments.of("/event-stream/paged?pageNumber=2", new PartialUrl("event-stream/paged", "", new PageNumber(2))),
					Arguments.of("/event-stream/by-time?year=2024&month=06&day=28&pageNumber=2", new PartialUrl("event-stream/by-time", "year=2024&month=06&day=28", new PageNumber(2)))
			);
		}
	}

	static class ChildPartialUrlProvider implements ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
			return Stream.of(
					Arguments.of(new PartialUrl("event-stream/paged", "", null), new PartialUrl("event-stream/paged", "", new PageNumber(1))),
					Arguments.of(new PartialUrl("event-stream/by-loc", "tile=15/142/123", null), new PartialUrl("event-stream/by-loc", "tile=15/142/123", new PageNumber(1))),
					Arguments.of(new PartialUrl("event-stream/by-time", "year=2024&month=06&day=28", null), new PartialUrl("event-stream/by-time", "year=2024&month=06&day=28", new PageNumber(1))),
					Arguments.of(new PartialUrl("event-stream/paged", "", new PageNumber(2)), new PartialUrl("event-stream/paged", "", new PageNumber(3))),
					Arguments.of(new PartialUrl("event-stream/by-time", "year=2024&month=06&day=28", new PageNumber(2)), new PartialUrl("event-stream/by-time", "year=2024&month=06&day=28", new PageNumber(3)))
			);
		}
	}
}