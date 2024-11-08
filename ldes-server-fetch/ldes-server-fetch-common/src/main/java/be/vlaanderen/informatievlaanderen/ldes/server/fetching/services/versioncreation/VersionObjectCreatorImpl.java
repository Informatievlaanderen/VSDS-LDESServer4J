package be.vlaanderen.informatievlaanderen.ldes.server.fetching.services.versioncreation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.VersionObjectModelBuilder;
import org.apache.jena.rdf.model.Model;

import java.time.LocalDateTime;

public class VersionObjectCreatorImpl implements VersionObjectCreator {
    private final String versionOfPath;
    private final String timestampPath;

    public VersionObjectCreatorImpl(String versionOfPath, String timestampPath) {
        this.versionOfPath = versionOfPath;
        this.timestampPath = timestampPath;
    }

    @Override
    public Model createFromMember(String subject, Model model, String versionOf, LocalDateTime timestamp) {
        return VersionObjectModelBuilder.create()
                .withMemberSubject(subject)
                .withVersionOfProperties(versionOfPath, versionOf)
                .withTimestampProperties(timestampPath, timestamp)
                .withModel(model)
                .buildVersionObjectModel();
    }


}
