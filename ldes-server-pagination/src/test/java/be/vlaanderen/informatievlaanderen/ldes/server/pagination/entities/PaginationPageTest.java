package be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.PageNumber;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PaginationPageTest {
	private static final String VIEW_NAME_PREFIX = "/mobility-hindrances/paged";

	@ParameterizedTest
	@ArgumentsSource(BucketProvider.class)
	void test_GetPartialUrl(String bucketDescriptor, String expectedPartialUrl) {
		final PageNumber pageNumber = new PageNumber(1);
		final PaginationPage page = new PaginationPage(0, VIEW_NAME_PREFIX, new Bucket(0, bucketDescriptor), pageNumber, 12);

		final String result = page.getPartialUrl();

		assertThat(result).isEqualTo(expectedPartialUrl);
	}

	static class BucketProvider implements ArgumentsProvider {
		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(
					Arguments.of("year=2024&month=06&day=18", VIEW_NAME_PREFIX + "?year=2024&month=06&day=18&pageNumber=1"),
					Arguments.of("", VIEW_NAME_PREFIX + "?pageNumber=1")
			);
		}
	}

}