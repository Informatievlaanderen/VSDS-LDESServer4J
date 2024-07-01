package be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects;

public class PageNumber {
	private final int value;

	public PageNumber(int value) {
		this.value = value;
	}

	public String asUrlPart() {
		return "&pageNumber=%d".formatted(value);
	}

	public PageNumber increment() {
		return new PageNumber(value + 1);
	}

	public static PageNumber startPageNumber() {
		return new PageNumber(1);
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PageNumber that)) return false;

		return value == that.value;
	}

	@Override
	public int hashCode() {
		return value;
	}
}
