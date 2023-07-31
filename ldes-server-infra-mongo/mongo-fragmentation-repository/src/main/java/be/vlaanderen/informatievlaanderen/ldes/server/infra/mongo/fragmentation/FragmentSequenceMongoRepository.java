package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentSequence;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentSequenceRepository;
import org.springframework.stereotype.Component;

@Component
// TODO TVB: 29/07/23 IMPL ME as proper repo
public class FragmentSequenceMongoRepository implements FragmentSequenceRepository {

	FragmentSequence fragmentSequence;

	@Override
	public FragmentSequence findLastProcessedSequence(ViewName viewName) {
		return fragmentSequence != null ? fragmentSequence : new FragmentSequence(viewName, 0);
	}

	@Override
	public void saveLastProcessedSequence(FragmentSequence sequence) {
		this.fragmentSequence = sequence;
	}

}
