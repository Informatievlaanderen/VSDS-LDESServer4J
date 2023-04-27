package be.vlaanderen.informatievlaanderen.ldes.server.ingest.events;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;

public class MemberIngestedEvent {

    private final Member ingestedMember;

    public MemberIngestedEvent(Member ingestedMember) {
        this.ingestedMember = ingestedMember;
    }

    public Member getIngestedMember() {
        return ingestedMember;
    }

}
