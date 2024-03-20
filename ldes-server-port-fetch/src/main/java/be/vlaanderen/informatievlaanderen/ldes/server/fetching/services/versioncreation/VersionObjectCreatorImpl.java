package be.vlaanderen.informatievlaanderen.ldes.server.fetching.services.versioncreation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.VersionObjectModelBuilder;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import org.apache.jena.rdf.model.Model;

public class VersionObjectCreatorImpl implements VersionObjectCreator {
    private final String versionOfPath;
    private final String timestampPath;

    public VersionObjectCreatorImpl(String versionOfPath, String timestampPath) {
        this.versionOfPath = versionOfPath;
        this.timestampPath = timestampPath;
    }

    @Override
    public Model createFromMember(Member member) {
        return VersionObjectModelBuilder.create()
                .withMemberId(member.getId())
                .withVersionOfProperties(versionOfPath, member.getVersionOf())
                .withTimestampProperties(timestampPath, member.getTimestamp())
                .withModel(member.getModel())
                .buildVersionObjectModel();
    }

}
