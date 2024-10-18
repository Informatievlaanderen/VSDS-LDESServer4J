package be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PageTest {
	private static final String TILE = "tile=15/142/122";

	@Test
	void given_PageWithBucketDescriptor_test_CreateWithPartialUrl() {
		final String partialUrl = "/mobility-hindrances/by-loc?%s&pageNumber=4".formatted(TILE);
		final Page expectedPage = new Page(0, 0, partialUrl, 150);

		final Page actualPage = Page.createWithPartialUrl(0, 0, partialUrl, 0, 150);

		assertThat(actualPage)
				.usingRecursiveComparison()
				.isEqualTo(expectedPage);
	}

	@Test
	void given_PageWithoutBucketDescriptor_test_CreateWithPartialUrl() {
		final String partialUrl = "/mobility-hindrances/paged?pageNumber=4";
		final Page expectedPage = new Page(0, 0, partialUrl,150);

		final Page actualPage = Page.createWithPartialUrl(0, 0, partialUrl, 0, 150);

		assertThat(actualPage)
				.usingRecursiveComparison()
				.isEqualTo(expectedPage);
	}




}