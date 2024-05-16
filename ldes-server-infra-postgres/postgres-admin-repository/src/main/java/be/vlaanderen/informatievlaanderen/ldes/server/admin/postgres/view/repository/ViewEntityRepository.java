package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.repository;


import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.entity.ViewEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;

@Primary
public interface ViewEntityRepository extends JpaRepository<ViewEntity, String> {
}
