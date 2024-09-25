package be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.pagenumber;

import java.util.UUID;

public class UuidPageNumber implements PageNumber {
	private final String value;

	public UuidPageNumber(UUID uuid) {
		this.value = uuid.toString();
	}

	public UuidPageNumber() {
		this.value = UUID.randomUUID().toString();
	}

	public static UuidPageNumber fromString(String value) {
		final UUID uuid = UUID.fromString(value);
		return new UuidPageNumber(uuid);
	}

	@Override
	public String asString() {
		return PAGE_NUMBER + "=" + value;
	}

	@Override
	public PageNumber getNextPageNumber() {
		return new UuidPageNumber();
	}
}
