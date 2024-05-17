package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.repository.ShaclShapeRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.repository.ShaclShapeEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.service.ShaclShapeEntityConverter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class ShaclShapePostgresRepository implements ShaclShapeRepository {
	private final ShaclShapeEntityRepository repository;
	private final ShaclShapeEntityConverter converter = new ShaclShapeEntityConverter();

	public ShaclShapePostgresRepository(ShaclShapeEntityRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<ShaclShape> retrieveAllShaclShapes() {
		return repository.findAll()
				.stream()
				.map(converter::toShaclShape)
				.toList();
	}

	@Override
	public Optional<ShaclShape> retrieveShaclShape(String collectionName) {
		return repository.findById(collectionName).map(converter::toShaclShape);
	}

	@Override
	@Transactional
	public ShaclShape saveShaclShape(ShaclShape shaclShape) {
		repository.save(converter.fromShaclShape(shaclShape));
		return shaclShape;
	}

	@Override
	public void deleteShaclShape(String collectionName) {
		repository.deleteById(collectionName);
	}
}
