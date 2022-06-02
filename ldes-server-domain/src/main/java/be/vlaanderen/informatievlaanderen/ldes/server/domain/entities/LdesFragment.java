package be.vlaanderen.informatievlaanderen.ldes.server.domain.entities;

import java.util.List;

public class LdesFragment {

    private final List<LdesMember> members;

    public LdesFragment(List<LdesMember> members) {
        this.members = members;
    }

    public List<LdesMember> getMembers() {
        return members;
    }
}
