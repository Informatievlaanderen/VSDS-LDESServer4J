package be.vlaanderen.informatievlaanderen.ldes.server.retention.integrationtest.stub;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timeandversionbased.TimeAndVersionBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.versionbased.VersionBasedRetentionPolicy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Component
public class InMemoryMemberPropertiesRepository implements MemberPropertiesRepository {

    private final ConcurrentHashMap<String, MemberProperties> memberPropertiesMap;

    public InMemoryMemberPropertiesRepository() {
        this.memberPropertiesMap = new ConcurrentHashMap<>();
    }

    @Override
    public void insertAll(List<MemberProperties> memberProperties) {
        memberProperties.forEach(this::insert);
    }

    private void insert(MemberProperties memberProperties) {
        if (memberPropertiesMap.containsKey(memberProperties.getId())) {
            throw new IllegalArgumentException("MemberProperties already inserted!");
        }
        memberPropertiesMap.put(memberProperties.getId(), memberProperties);
    }

    @Override
    public Optional<MemberProperties> retrieve(String id) {
        if (memberPropertiesMap.containsKey(id)) {
            return Optional.of(memberPropertiesMap.get(id));
        }
        return Optional.empty();
    }

    @Override
    public void addViewToAll(ViewName viewName) {
        memberPropertiesMap.forEach((key, val) -> val.addViewReference(viewName.asString()));
    }

    @Override
    public List<MemberProperties> getMemberPropertiesOfVersionAndView(String versionOf, String viewName) {
        return memberPropertiesMap
                .values()
                .stream()
                .filter(memberProperties -> memberProperties.getVersionOf().equals(versionOf))
                .filter(memberProperties -> memberProperties.getViewReferences().contains(viewName))
                .toList();
    }

    @Override
    public Stream<MemberProperties> getMemberPropertiesWithViewReference(ViewName viewName) {
        return memberPropertiesMap
                .values()
                .stream()
                .filter(memberProperties -> memberProperties.getViewReferences().contains(viewName.asString()));
    }

    @Override
    public void removeViewReference(String id, String viewName) {
        memberPropertiesMap.get(id).deleteViewReference(viewName);
    }

    @Override
    public void removeMemberPropertiesOfCollection(String collectionName) {
        List<MemberProperties> properties = memberPropertiesMap
                .values()
                .stream()
                .filter(memberProperties -> memberProperties.getCollectionName().equals(collectionName))
                .toList();
        properties.forEach(memberProperties -> memberPropertiesMap.remove(memberProperties.getId()));
    }

    @Override
    public void deleteById(String id) {
        memberPropertiesMap.remove(id);
    }

    @Override
    public Stream<MemberProperties> findExpiredMemberProperties(ViewName viewName,
                                                                TimeBasedRetentionPolicy policy) {
        return Stream.of(
                memberPropertiesMap.get("http://test-data/mobility-hindrances/1/1"),
                memberPropertiesMap.get("http://test-data/mobility-hindrances/2/1"),
                memberPropertiesMap.get("http://test-data/mobility-hindrances/2/2"),
                memberPropertiesMap.get("http://test-data/mobility-hindrances/3/1"),
                memberPropertiesMap.get("http://test-data/mobility-hindrances/3/2"),
                memberPropertiesMap.get("http://test-data/mobility-hindrances/3/3"),
                memberPropertiesMap.get("http://test-data/mobility-hindrances/3/4")
        );
    }

    @Override
    public Stream<MemberProperties> findExpiredMemberProperties(ViewName viewName,
                                                                VersionBasedRetentionPolicy policy) {
        return Stream.of(
                memberPropertiesMap.get("http://test-data/mobility-hindrances/1/1"),
                memberPropertiesMap.get("http://test-data/mobility-hindrances/2/1"),
                memberPropertiesMap.get("http://test-data/mobility-hindrances/2/2"),
                memberPropertiesMap.get("http://test-data/mobility-hindrances/3/1"),
                memberPropertiesMap.get("http://test-data/mobility-hindrances/3/2"),
                memberPropertiesMap.get("http://test-data/mobility-hindrances/3/3")
        );
    }

    @Override
    public Stream<MemberProperties> findExpiredMemberProperties(ViewName viewName,
                                                                TimeAndVersionBasedRetentionPolicy policy) {
        return Stream.of(
                memberPropertiesMap.get("http://test-data/mobility-hindrances/1/1"),
                memberPropertiesMap.get("http://test-data/mobility-hindrances/2/1"),
                memberPropertiesMap.get("http://test-data/mobility-hindrances/2/2"),
                memberPropertiesMap.get("http://test-data/mobility-hindrances/3/1"),
                memberPropertiesMap.get("http://test-data/mobility-hindrances/3/2"),
                memberPropertiesMap.get("http://test-data/mobility-hindrances/3/3"),
                memberPropertiesMap.get("http://test-data/mobility-hindrances/3/4")
        );
    }
}
