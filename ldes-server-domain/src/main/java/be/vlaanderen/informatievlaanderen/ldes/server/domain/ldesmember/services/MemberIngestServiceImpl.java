package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationMediator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import io.micrometer.core.instrument.Metrics;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MemberIngestServiceImpl implements MemberIngestService {

	private final LdesMemberRepository ldesMemberRepository;

	private final FragmentationMediator fragmentationMediator;

	public MemberIngestServiceImpl(LdesMemberRepository ldesMemberRepository,
			FragmentationMediator fragmentationMediator) {
		this.ldesMemberRepository = ldesMemberRepository;
		this.fragmentationMediator = fragmentationMediator;
	}

	@Override
	public void addMember(LdesMember ldesMember) {
		Optional<LdesMember> optionalLdesMember = ldesMemberRepository.getLdesMemberById(ldesMember.getLdesMemberId());
		if (optionalLdesMember.isEmpty()) {
			Metrics.counter("ldes_server_ingested_members_count").increment();
			LdesMember storedLdesMember = storeLdesMember(ldesMember);
			fragmentationMediator.addMemberToFragment(storedLdesMember);
		}
	}

	private LdesMember storeLdesMember(LdesMember ldesMember) {
		ldesMember.removeTreeMember();
		return ldesMemberRepository.saveLdesMember(ldesMember);
	}
}
