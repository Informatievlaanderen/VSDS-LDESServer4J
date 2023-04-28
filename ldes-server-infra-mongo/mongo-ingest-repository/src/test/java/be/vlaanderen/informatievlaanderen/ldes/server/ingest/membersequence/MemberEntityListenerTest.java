package be.vlaanderen.informatievlaanderen.ldes.server.ingest.membersequence;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.MemberEntity;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class MemberEntityListenerTest {

    private final SequenceGeneratorService sequenceGeneratorService = mock(SequenceGeneratorService.class);
    private final MemberEntityListener ldesMemberEntityListener = new MemberEntityListener(
            sequenceGeneratorService);

    @Test
    void test_MemberHasNoIndex() {
        MemberEntity ldesMemberEntity = new MemberEntity("id", "collectionName", null, "model");
        BeforeConvertEvent<MemberEntity> beforeConvertEvent = new BeforeConvertEvent<>(ldesMemberEntity,
                "collection");

        ldesMemberEntityListener.onBeforeConvert(beforeConvertEvent);

        verify(sequenceGeneratorService, times(1)).generateSequence("collectionName");
    }

    @Test
    void test_MemberHasIndex() {
        MemberEntity ldesMemberEntity = new MemberEntity("id", "collectionName", 23L, "model");
        BeforeConvertEvent<MemberEntity> beforeConvertEvent = new BeforeConvertEvent<>(ldesMemberEntity,
                "collection");

        ldesMemberEntityListener.onBeforeConvert(beforeConvertEvent);

        verifyNoInteractions(sequenceGeneratorService);
    }

}