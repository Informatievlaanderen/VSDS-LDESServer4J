package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.entity.ViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ViewEntityRepository  extends JpaRepository<ViewEntity, Integer> {
    @Query("SELECT v FROM ViewEntity v WHERE v.eventStream.name = :collectionName")
    List<ViewEntity> findAllByCollectionName(String collectionName);

    @Query("SELECT v FROM ViewEntity v WHERE v.name = :viewName AND v.eventStream.name = :collectionName")
    Optional<ViewEntity> findByViewName(String collectionName, String viewName);

    @Modifying
    @Query("DELETE FROM ViewEntity v WHERE v.name = :viewName AND v.eventStream.name = :collectionName")
    void deleteByViewName(String collectionName, String viewName);
}
