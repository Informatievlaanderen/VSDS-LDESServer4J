package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentSequence;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentSequenceRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.mapper.SequenceEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.repository.SequenceEntityRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FragmentSequenceMongoRepository implements FragmentSequenceRepository {

	private static final String COLLECTION_VIEW_SEPARATOR = "/";

	private final SequenceEntityRepository sequenceEntityRepository;
	private final SequenceEntityMapper sequenceEntityMapper;

	public FragmentSequenceMongoRepository(SequenceEntityRepository sequenceEntityRepository,
			SequenceEntityMapper sequenceEntityMapper) {
		this.sequenceEntityRepository = sequenceEntityRepository;
		this.sequenceEntityMapper = sequenceEntityMapper;
	}

	@Override
	public Optional<FragmentSequence> findLastProcessedSequence(ViewName viewName) {
		return sequenceEntityRepository
				.findById(viewName.asString())
				.map(sequenceEntityMapper::toFragmentSequence);
	}

	@Override
	public void saveLastProcessedSequence(FragmentSequence sequence) {
		sequenceEntityRepository.save(sequenceEntityMapper.toEntity(sequence));
	}

	@Override
	public void deleteByViewName(ViewName viewName) {
		sequenceEntityRepository.deleteById(viewName.asString());
	}

	@Override
	public void deleteByCollection(String collectionName) {
		sequenceEntityRepository.deleteAllByViewNameStartingWith(collectionName + COLLECTION_VIEW_SEPARATOR);
	}

}
