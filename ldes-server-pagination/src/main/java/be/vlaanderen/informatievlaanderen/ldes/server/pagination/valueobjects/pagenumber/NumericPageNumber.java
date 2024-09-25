package be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.pagenumber;

public class NumericPageNumber implements PageNumber {
	private final int value;

	public NumericPageNumber(int value) {
		this.value = value;
	}

	@Override
	public String asString() {
		return PAGE_NUMBER + "=" + value;
	}

	@Override
	public PageNumber getNextPageNumber() {
		return new NumericPageNumber(value + 1);
	}

	public static PageNumber startPageNumber() {
		return new NumericPageNumber(1);
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof NumericPageNumber that)) return false;

		return value == that.value;
	}

	@Override
	public int hashCode() {
		return value;
	}
}
