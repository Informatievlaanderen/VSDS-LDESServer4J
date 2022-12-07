package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest.exceptions;

public class MalformedMemberIdException extends RuntimeException {

	public MalformedMemberIdException() {
		super();
	}

	@Override
	public String getMessage() {
		return "Member id could not be extracted. MemberType could not be found in listStatements.";
	}

}
