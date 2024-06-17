package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

@Service
public class IngestEventSourceServiceImpl implements IngestEventSourceService {

	private final MemberRepository memberRepository;

	public IngestEventSourceServiceImpl(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Override
	public Stream<IngestedMember> getMemberStreamOfCollection(String collectionName) {
		return memberRepository.getMemberStreamOfCollection(collectionName);
	}

}
