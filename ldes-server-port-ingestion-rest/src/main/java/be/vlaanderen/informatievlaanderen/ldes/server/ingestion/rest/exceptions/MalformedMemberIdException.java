package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest.exceptions;

public class MalformedMemberIdException extends RuntimeException {

	private String expectedMemberType;

	public MalformedMemberIdException(String expectedMemberType) {
		super();
		this.expectedMemberType = expectedMemberType;
	}

	@Override
	public String getMessage() {
		return String.format("Member id could not be extracted. MemberType %s could not be found in listStatements.",
				expectedMemberType);
	}

}
