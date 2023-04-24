package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.exception;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services.GreaterOrEqualsPageFilter.PAGE_NUMBER_KEY;

public class GreaterOrEqualsPageFilterException extends RuntimeException {
	final String fragmentId;

	public GreaterOrEqualsPageFilterException(String fragmentId) {
		this.fragmentId = fragmentId;
	}

	@Override
	public String getMessage() {
		return "Could not create filter starting from fragment: " + fragmentId + " No value for key " + PAGE_NUMBER_KEY
				+ " in fragment pairs.";
	}
}
