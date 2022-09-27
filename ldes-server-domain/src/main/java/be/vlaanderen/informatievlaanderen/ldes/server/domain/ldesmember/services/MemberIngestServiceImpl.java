package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationMediator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MemberIngestServiceImpl implements MemberIngestService {

	private final LdesMemberRepository ldesMemberRepository;

	private final FragmentationMediator fragmentationMediator;

	private final MeterRegistry registry;

	public MemberIngestServiceImpl(LdesMemberRepository ldesMemberRepository,
			FragmentationMediator fragmentationMediator,
			final MeterRegistry meterRegistry) {
		this.ldesMemberRepository = ldesMemberRepository;
		this.fragmentationMediator = fragmentationMediator;
		this.registry = meterRegistry;
	}

	@Override
	public void addMember(LdesMember ldesMember) {
		Optional<LdesMember> optionalLdesMember = ldesMemberRepository.getLdesMemberById(ldesMember.getLdesMemberId());
		if (optionalLdesMember.isEmpty()) {
			registry.counter("ldes_server_ingested_members").increment();
			LdesMember storedLdesMember = storeLdesMember(ldesMember);
			fragmentationMediator.addMemberToFragment(storedLdesMember);
		}
	}

	private LdesMember storeLdesMember(LdesMember ldesMember) {
		ldesMember.removeTreeMember();
		return ldesMemberRepository.saveLdesMember(ldesMember);
	}
}
