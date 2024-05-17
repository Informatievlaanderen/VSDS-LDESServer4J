package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataservice.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataservice.entity.DataServiceEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;

@Primary
public interface DataServiceEntityRepository extends JpaRepository<DataServiceEntity, String> {
	void deleteAllByViewNameStartingWith(String prefix);
}
