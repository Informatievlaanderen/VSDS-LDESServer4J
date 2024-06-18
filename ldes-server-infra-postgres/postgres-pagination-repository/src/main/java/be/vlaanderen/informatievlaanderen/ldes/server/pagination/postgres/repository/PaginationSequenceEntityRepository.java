package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository;


import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PaginationSequenceEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;

@Primary
public interface PaginationSequenceEntityRepository extends JpaRepository<PaginationSequenceEntity, String> {
    void deleteAllByViewNameStartingWith(String collectionName);
}
