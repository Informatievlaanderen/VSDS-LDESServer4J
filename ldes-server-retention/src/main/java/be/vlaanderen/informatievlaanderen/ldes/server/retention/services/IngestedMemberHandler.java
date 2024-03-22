package be.vlaanderen.informatievlaanderen.ldes.server.retention.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MembersIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.ViewCollection;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IngestedMemberHandler {

    private final MemberPropertiesRepository memberPropertiesRepository;
    private final ViewCollection viewCollection;

    public IngestedMemberHandler(MemberPropertiesRepository memberPropertiesRepository,
                                 ViewCollection viewCollection) {
        this.memberPropertiesRepository = memberPropertiesRepository;
        this.viewCollection = viewCollection;
    }

    @EventListener
    public void handleMembersIngestedEvent(MembersIngestedEvent event) {
        final List<MemberProperties> members = event.members().stream()
                .map(member -> new MemberProperties(member.id(), event.collectionName(), member.versionOf(), member.timestamp()))
                .toList();

        addViewsToMembers(members);
        memberPropertiesRepository.insertAll(members);
    }

    private void addViewsToMembers(List<MemberProperties> members) {
        final List<String> viewNames = viewCollection.getViews().stream()
                .map(ViewSpecification::getName)
                .map(ViewName::asString)
                .toList();

        members.forEach(member -> member.addAllViewReferences(viewNames));
    }

}
