package be.vlaanderen.informatievlaanderen.ldes.server.domain.entities;

import java.util.List;

public class LdesFragment {

    public LdesFragment(List<LdesMember> records) {
        this.records = records;
    }

    private final List<LdesMember> records;

    public List<LdesMember> getRecords() {
        return records;
    }
}
