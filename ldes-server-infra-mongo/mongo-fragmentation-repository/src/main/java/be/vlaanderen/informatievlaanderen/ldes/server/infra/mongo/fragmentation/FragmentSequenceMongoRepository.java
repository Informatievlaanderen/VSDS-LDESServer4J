package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentSequence;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentSequenceRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.MemberToFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.mapper.MemberToFragmentEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.repository.MemberToFragmentEntityRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
// TODO TVB: 29/07/23 IMPL ME as proper repo
public class FragmentSequenceMongoRepository implements FragmentSequenceRepository {

	FragmentSequence fragmentSequence;

	@Override
	public FragmentSequence findLastProcessedSequence(ViewName viewName) {
		return fragmentSequence != null ? fragmentSequence : new FragmentSequence(viewName, 60000);
	}

	@Override
	public void saveLastProcessedSequence(FragmentSequence sequence) {
		this.fragmentSequence = sequence;
	}

}
