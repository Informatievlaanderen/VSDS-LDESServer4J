package be.vlaanderen.informatievlaanderen.ldes.server.ingest.collection;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor.MemberExtractor;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor.StateObjectMemberExtractor;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor.VersionObjectMemberExtractor;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.skolemization.SkolemizedMemberExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class MemberExtractorCollectionImplTest {
    private static final String COLLECTION_NAME = "collection";
    private MemberExtractorCollectionImpl memberExtractorCollection;
    private MemberExtractor memberExtractor;

    @BeforeEach
    void setUp() {
        memberExtractorCollection = new MemberExtractorCollectionImpl();
        memberExtractor = new VersionObjectMemberExtractor(COLLECTION_NAME, "versionOf", "timestamp");
        memberExtractorCollection.addMemberExtractor(COLLECTION_NAME, memberExtractor);
    }

    @Test
    void test_GetNonExisting() {
        Optional<MemberExtractor> fetchedVersionObjectTransformer = memberExtractorCollection
                .getMemberExtractor("non-existing-col");

        assertThat(fetchedVersionObjectTransformer).isEmpty();
    }

    @Test
    void test_Get() {
        Optional<MemberExtractor> fetchedVersionObjectTransformer = memberExtractorCollection
                .getMemberExtractor(COLLECTION_NAME);

        assertThat(fetchedVersionObjectTransformer).contains(memberExtractor);
    }

    @Test
    void test_HandleEventStreamDeletedEvent() {
        assertThat(memberExtractorCollection.getMemberExtractor(COLLECTION_NAME))
                .isPresent();

        memberExtractorCollection.handleEventStreamDeletedEvent(new EventStreamDeletedEvent(COLLECTION_NAME));

        assertThat(memberExtractorCollection.getMemberExtractor(COLLECTION_NAME))
                .isEmpty();
    }

    @Test
    void test_HandleVersionObjectEventStreamCreatedEvent() {
        final EventStream eventStream = new EventStream(COLLECTION_NAME, "timestampPath", "versionOfPath", false);

        memberExtractorCollection.handleEventStreamCreatedEvent(new EventStreamCreatedEvent(eventStream));

        assertThat(memberExtractorCollection.getMemberExtractor(COLLECTION_NAME))
                .containsInstanceOf(VersionObjectMemberExtractor.class);
    }

    @Test
    void test_HandleStateObjectEventStreamCreatedEvent() {
        final EventStream eventStream = new EventStream(COLLECTION_NAME, "timestampPath", "versionOfPath", true);

        memberExtractorCollection.handleEventStreamCreatedEvent(new EventStreamCreatedEvent(eventStream));

        assertThat(memberExtractorCollection.getMemberExtractor(COLLECTION_NAME))
                .containsInstanceOf(StateObjectMemberExtractor.class);
    }

    @Test
    void test_HandleStateObjectSkolemizationDomeinEventStreamCreatedEvent() {
        final EventStream eventStream = new EventStream(COLLECTION_NAME, "timestampPath", "versionOfPath", true, "http://example.org");

        memberExtractorCollection.handleEventStreamCreatedEvent(new EventStreamCreatedEvent(eventStream));

        assertThat(memberExtractorCollection.getMemberExtractor(COLLECTION_NAME))
                .containsInstanceOf(SkolemizedMemberExtractor.class)
                .get()
                .extracting(extractor -> {
	                try {
		                Field field = extractor.getClass().getSuperclass().getDeclaredField("baseMemberExtractor");
                        field.setAccessible(true);
                        return field.get(extractor);
	                } catch (Exception e) {
		                throw new RuntimeException(e);
	                }
                })
                .isInstanceOf(StateObjectMemberExtractor.class);
    }

    @Test
    void test_HandleVersionObjectsSkolemizationDomeinEventStreamCreatedEvent() {
        final EventStream eventStream = new EventStream(COLLECTION_NAME, "timestampPath", "versionOfPath", false, "http://example.org");

        memberExtractorCollection.handleEventStreamCreatedEvent(new EventStreamCreatedEvent(eventStream));

        assertThat(memberExtractorCollection.getMemberExtractor(COLLECTION_NAME))
                .containsInstanceOf(SkolemizedMemberExtractor.class)
                .get()
                .extracting(extractor -> {
                    try {
                        Field field = extractor.getClass().getSuperclass().getDeclaredField("baseMemberExtractor");
                        field.setAccessible(true);
                        return field.get(extractor);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .isInstanceOf(VersionObjectMemberExtractor.class);
    }
}