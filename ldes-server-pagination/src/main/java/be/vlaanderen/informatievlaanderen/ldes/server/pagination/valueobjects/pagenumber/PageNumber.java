package be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.pagenumber;

public interface PageNumber {
	String PAGE_NUMBER = "pageNumber";

	String asString();

	PageNumber getNextPageNumber();
}
