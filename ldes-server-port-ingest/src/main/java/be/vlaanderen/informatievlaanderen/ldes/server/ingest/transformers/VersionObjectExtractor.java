package be.vlaanderen.informatievlaanderen.ldes.server.ingest.transformers;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.LocalDateTimeConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.exceptions.MemberIdNotFoundException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class VersionObjectExtractor implements VersionObjectTransformer {
    private final LocalDateTimeConverter localDateTimeConverter = new LocalDateTimeConverter();
    private final String collectionName;
    private final String versionOfPath;
    private final String timestampPath;

    public VersionObjectExtractor(String collectionName, String versionOfPath, String timestampPath) {
        this.collectionName = collectionName;
        this.versionOfPath = versionOfPath;
        this.timestampPath = timestampPath;
    }

    @Override
    public List<Member> transformToVersionObjects(Model ingestedModel) {
        final String memberId = extractMemberId(ingestedModel);
        final String transactionId = UUID.randomUUID().toString();
        final String versionOf = extractVersionOf(ingestedModel);
        final LocalDateTime timestamp = extractTimestamp(ingestedModel);
        final Member member = new Member(memberId, collectionName, versionOf, timestamp, null, transactionId, ingestedModel);
        return List.of(member);
    }

    private String extractVersionOf(Model model) {
        return model
                .listObjectsOfProperty(ResourceFactory.createProperty(versionOfPath))
                .nextOptional()
                .map(RDFNode::toString)
                .orElseThrow(() -> new IllegalStateException("Ingested model does not contain expected %s".formatted(versionOfPath)));
    }

    private LocalDateTime extractTimestamp(Model model) {
        return model
                .listObjectsOfProperty(ResourceFactory.createProperty(timestampPath))
                .nextOptional()
                .map(RDFNode::asLiteral)
                .map(localDateTimeConverter::getLocalDateTime)
                .orElseThrow(() -> new IllegalStateException("Ingested model does not contain expected %s".formatted(timestampPath)));
    }

    private String extractMemberId(Model model) {
        final var ids = model.listSubjectsWithProperty(ResourceFactory.createProperty(versionOfPath)).toSet();
        if (ids.size() != 1) {
            throw new MemberIdNotFoundException(model);
        } else {
            return collectionName + "/" + ids.iterator().next().toString();
        }
    }
}
