package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.shaclshape;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.repository.ShaclShapeRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.shaclshape.repository.ShaclShapeEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.shaclshape.service.ShaclShapeEntityConverter;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ShaclShapeMongoRepository implements ShaclShapeRepository {
	private final ShaclShapeEntityRepository repository;
	private final ShaclShapeEntityConverter converter = new ShaclShapeEntityConverter();

	public ShaclShapeMongoRepository(ShaclShapeEntityRepository repository) {
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
	public ShaclShape saveShaclShape(ShaclShape shaclShape) {
		repository.save(converter.fromShaclShape(shaclShape));
		return shaclShape;
	}

	@Override
	public void deleteShaclShape(String collectionName) {
		repository.deleteById(collectionName);
	}
}
