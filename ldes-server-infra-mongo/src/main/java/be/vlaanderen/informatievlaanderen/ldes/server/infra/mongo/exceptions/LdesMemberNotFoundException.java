package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.exceptions;

public class LdesMemberNotFoundException extends RuntimeException{


    private final String memberId;

    public LdesMemberNotFoundException(String memberId) {
        super();
        this.memberId = memberId;
    }

    @Override
    public String getMessage() {
        return String.format("LdesMember %s not found in database.", memberId);
    }
}
