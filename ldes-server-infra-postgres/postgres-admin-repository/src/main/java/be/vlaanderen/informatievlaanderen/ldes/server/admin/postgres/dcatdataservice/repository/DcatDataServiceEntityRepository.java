package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataservice.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataservice.entity.DcatDataServiceEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

@Primary
public interface DcatDataServiceEntityRepository extends JpaRepository<DcatDataServiceEntity, Integer> {
	@Query("SELECT d FROM DcatDataServiceEntity d WHERE d.viewEntity.name = :viewName AND d.viewEntity.eventStream.name = :collectionName")
	Optional<DcatDataServiceEntity> findByViewName(String collectionName, String viewName);

	@Modifying
	@Query("DELETE FROM DcatDataServiceEntity d WHERE d.viewEntity.name = :viewName AND d.viewEntity.eventStream.name = :collectionName")
	void deleteByViewName(String collectionName, String viewName);
}
