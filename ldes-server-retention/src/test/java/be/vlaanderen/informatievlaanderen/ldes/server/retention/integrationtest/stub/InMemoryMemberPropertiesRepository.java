package be.vlaanderen.informatievlaanderen.ldes.server.retention.integrationtest.stub;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
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
	public void saveMemberPropertiesWithoutViews(MemberProperties memberProperties) {
		memberPropertiesMap.put(memberProperties.getId(), memberProperties);
	}

	@Override
	public void save(MemberProperties memberProperties) {
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
	public void addViewReference(String id, String viewName) {
		memberPropertiesMap.get(id).addViewReference(viewName);
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
	public Stream<MemberProperties> getMemberPropertiesWithViewReference(String viewName) {
		return memberPropertiesMap
				.values()
				.stream()
				.filter(memberProperties -> memberProperties.getViewReferences().contains(viewName));
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
}
