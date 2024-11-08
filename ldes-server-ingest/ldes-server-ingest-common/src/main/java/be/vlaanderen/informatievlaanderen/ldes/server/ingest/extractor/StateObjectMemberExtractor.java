package be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import org.apache.jena.rdf.model.Model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class StateObjectMemberExtractor implements MemberExtractor {
    private final String collectionName;
    private final String delimiter;

    public StateObjectMemberExtractor(String collectionName, String delimiter) {
        this.collectionName = collectionName;
	    this.delimiter = delimiter;
    }

    @Override
    public List<IngestedMember> extractMembers(Model ingestedModel) {
        final String txId = UUID.randomUUID().toString();
        final LocalDateTime ingestedTimestamp = LocalDateTime.now();

        return MemberModelExtractor.initialize(ingestedModel).extractAllMemberModels().stream()
                .map(memberModel -> memberModel.mapToMember(collectionName, delimiter, ingestedTimestamp, txId))
                .toList();
    }
}
