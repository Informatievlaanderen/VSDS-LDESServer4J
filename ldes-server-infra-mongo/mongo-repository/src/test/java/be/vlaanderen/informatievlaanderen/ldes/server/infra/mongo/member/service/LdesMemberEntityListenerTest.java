package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.service;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.entity.LdesMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.membersequence.service.LegacySequenceGeneratorService;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

class LdesMemberEntityListenerTest {
	private final LegacySequenceGeneratorService sequenceGeneratorService = mock(
			LegacySequenceGeneratorService.class);
	private final LdesMemberEntityListener ldesMemberEntityListener = new LdesMemberEntityListener(
			sequenceGeneratorService);

	@Test
	void test_MemberHasNoIndex() {
		LdesMemberEntity ldesMemberEntity = new LdesMemberEntity("id", "collectionName", null, "versionOf",
				LocalDateTime.now(), "model", List.of());
		BeforeConvertEvent<LdesMemberEntity> beforeConvertEvent = new BeforeConvertEvent<>(ldesMemberEntity,
				"collection");

		ldesMemberEntityListener.onBeforeConvert(beforeConvertEvent);

		verify(sequenceGeneratorService, times(1)).generateSequence("collectionName");
	}

	@Test
	void test_MemberHasIndex() {
		LdesMemberEntity ldesMemberEntity = new LdesMemberEntity("id", "collectionName", 23L, "versionOf",
				LocalDateTime.now(), "model", List.of());
		BeforeConvertEvent<LdesMemberEntity> beforeConvertEvent = new BeforeConvertEvent<>(ldesMemberEntity,
				"collection");

		ldesMemberEntityListener.onBeforeConvert(beforeConvertEvent);

		verifyNoInteractions(sequenceGeneratorService);
	}

}