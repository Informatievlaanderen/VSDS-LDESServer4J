package be.vlaanderen.informatievlaanderen.ldes.server.ingest.membersequence;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.MemberEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.MemberEntity;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class IngestMemberSequenceService {

	// TODO TVB: mongock to remove memberSequence entity

	private final MemberEntityRepository memberEntityRepository;
	private final Map<String, AtomicLong> sequenceByCollection;

	public IngestMemberSequenceService(MemberEntityRepository memberEntityRepository) {
		this.sequenceByCollection = new HashMap<>();
		this.memberEntityRepository = memberEntityRepository;
	}

	@EventListener
	public void handleEventStreamCreated(EventStreamCreatedEvent event) {
		final String collectionName = event.eventStream().getCollection();
		final AtomicLong sequence = memberEntityRepository
				.findFirstByCollectionNameOrderBySequenceNrDesc(collectionName)
				.map(MemberEntity::getSequenceNr)
				.map(AtomicLong::new)
				.orElse(new AtomicLong());
		sequenceByCollection.put(collectionName, sequence);
	}

	long generateNextSequence(String collectionName) {
		return sequenceByCollection.get(collectionName).incrementAndGet();
	}

	public void removeSequence(String collectionName) {
		sequenceByCollection.remove(collectionName);
	}

}
