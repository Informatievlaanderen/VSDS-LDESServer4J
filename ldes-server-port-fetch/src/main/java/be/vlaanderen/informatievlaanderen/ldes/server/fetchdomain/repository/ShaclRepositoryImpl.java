package be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.entities.Shacl;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ShaclRepositoryImpl implements ShaclRepository {
	private final Map<String, Shacl> shaclShapes = new HashMap<>();

	@Override
	public void saveShacl(Shacl shacl) {
		shaclShapes.put(shacl.getCollection(), shacl);
	}

	@Override
	public Shacl getShaclByCollection(String collection) {
		return shaclShapes.get(collection);
	}

	@Override
	public void deleteShaclByCollection(String collection) {
		shaclShapes.remove(collection);
	}
}
