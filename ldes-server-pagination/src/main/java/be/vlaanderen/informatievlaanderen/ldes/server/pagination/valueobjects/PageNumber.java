package be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects;

public class PageNumber {
	public static final String PAGE_NUMBER = "pageNumber";
	private final int value;

	public PageNumber(int value) {
		this.value = value;
	}

	public String asString() {
		return PAGE_NUMBER + "=" + value;
	}

	public PageNumber increment() {
		return new PageNumber(value + 1);
	}

	public static PageNumber startPageNumber() {
		return new PageNumber(1);
	}

	public static PageNumber fromString(String pageNumberString) {
		if (!pageNumberString.contains(PAGE_NUMBER)) {
			throw new IllegalArgumentException("Invalid page number: " + pageNumberString);
		}
		return new PageNumber(Integer.parseInt(pageNumberString.replace(PAGE_NUMBER + "=", "")));
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
