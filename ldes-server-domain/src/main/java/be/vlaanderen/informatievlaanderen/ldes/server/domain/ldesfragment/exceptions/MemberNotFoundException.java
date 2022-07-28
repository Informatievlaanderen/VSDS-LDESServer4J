package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.exceptions;

public class MemberNotFoundException extends RuntimeException {
    private final String ldesMemberId;

    public MemberNotFoundException(String ldesMemberId) {
        super();
        this.ldesMemberId = ldesMemberId;
    }

    @Override
    public String getMessage() {
        return "Member with id " + ldesMemberId + " not found in repository";
    }
}
