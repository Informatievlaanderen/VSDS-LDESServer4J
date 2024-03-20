package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services.membermapper;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.VersionObjectModelBuilder;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResourceFactory;

public class MemberMapper {
    private final String versionOfPath;
    private final String timestampPath;

    public MemberMapper(String versionOfPath, String timestampPath) {
        this.versionOfPath = versionOfPath;
        this.timestampPath = timestampPath;
    }

    public Member mapToFragmentationMember(be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member ingestMember) {
        return new Member(ingestMember.getId(), enrichModel(ingestMember), ingestMember.getSequenceNr());
    }

    private Model enrichModel(be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member ingestMember) {
        final String memberId = ingestMember.getId();
        final String subjectUri = memberId.startsWith("http") ? memberId : memberId.substring(memberId.indexOf("/") + 1);
        if(ingestMember.getModel().containsResource(ResourceFactory.createProperty(subjectUri))) {
            return ingestMember.getModel();
        }
        return VersionObjectModelBuilder.create()
                .withMemberId(memberId)
                .withVersionOfProperties(versionOfPath, ingestMember.getVersionOf())
                .withTimestampProperties(timestampPath, ingestMember.getTimestamp())
                .withModel(ingestMember.getModel())
                .buildVersionObjectModel();
    }
}
