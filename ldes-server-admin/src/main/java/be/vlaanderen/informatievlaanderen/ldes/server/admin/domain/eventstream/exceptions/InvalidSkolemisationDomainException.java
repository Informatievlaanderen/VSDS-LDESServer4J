package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.exceptions;

public class InvalidSkolemisationDomainException extends RuntimeException {
	public InvalidSkolemisationDomainException() {
		super();
	}

	@Override
	public String getMessage() {
		return "Invalid Skolemisation Domain. Should be URI";
	}
}
