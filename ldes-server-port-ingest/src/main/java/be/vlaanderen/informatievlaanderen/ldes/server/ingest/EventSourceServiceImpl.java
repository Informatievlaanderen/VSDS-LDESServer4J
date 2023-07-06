package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class EventSourceServiceImpl implements EventSourceService {

	private final MemberRepository memberRepository;

	public EventSourceServiceImpl(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	// TODO: 06/07/23 test
	@Override
	public Stream<Member> getMemberStreamOfCollection(String collectionName) {
		return memberRepository.getMemberStreamOfCollection(collectionName);
	}

}
