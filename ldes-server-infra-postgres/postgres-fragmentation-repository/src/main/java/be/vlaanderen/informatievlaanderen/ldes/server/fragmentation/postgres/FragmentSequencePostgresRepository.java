package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentSequence;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.mapper.SequenceEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository.SequenceEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentSequenceRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class FragmentSequencePostgresRepository implements FragmentSequenceRepository {

	private static final String COLLECTION_VIEW_SEPARATOR = "/";

	private final SequenceEntityRepository repository;
	private final SequenceEntityMapper mapper = new SequenceEntityMapper();

	public FragmentSequencePostgresRepository(SequenceEntityRepository repository) {
		this.repository = repository;
	}

	@Override
	public Optional<FragmentSequence> findLastProcessedSequence(ViewName viewName) {
		return repository
				.findById(viewName.asString())
				.map(mapper::toFragmentSequence);
	}

	@Override
	public void saveLastProcessedSequence(FragmentSequence sequence) {
		repository.save(mapper.toEntity(sequence));
	}

	@Override
	@Transactional
	public void deleteByViewName(ViewName viewName) {
		repository.deleteById(viewName.asString());
	}

	@Override
	@Transactional
	public void deleteByCollection(String collectionName) {
		repository.deleteAllByViewNameStartingWith(collectionName + COLLECTION_VIEW_SEPARATOR);
	}

}
