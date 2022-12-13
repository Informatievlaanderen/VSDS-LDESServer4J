package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest.exceptions;

public class MalformedMemberIdException extends RuntimeException {

	private final String expectedMemberType;

	public MalformedMemberIdException(String expectedMemberType) {
		super();
		this.expectedMemberType = expectedMemberType;
	}

	@Override
	public String getMessage() {
		return "Member id could not be extracted. MemberType " + expectedMemberType + " could not be found in listStatements.";
	}

}
