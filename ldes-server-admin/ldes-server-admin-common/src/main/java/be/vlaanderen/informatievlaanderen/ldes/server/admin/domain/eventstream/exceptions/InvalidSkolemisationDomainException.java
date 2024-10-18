package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.exceptions;

public class InvalidSkolemisationDomainException extends RuntimeException {
	private final String skolemizationDomain;
	public InvalidSkolemisationDomainException(String skolemizationDomain) {
		super();
		this.skolemizationDomain = skolemizationDomain;
	}

	@Override
	public String getMessage() {
		return String.format("Invalid Skolemisation Domain. Should be URI. Provided skolemizationDomain : %s", skolemizationDomain);
	}
}
