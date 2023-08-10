package be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.entities.Shacl;

public interface ShaclRepository {
	void saveShacl(Shacl shacl);

	Shacl getShaclByCollection(String collection);

	void deleteShaclByCollection(String collection);
}
