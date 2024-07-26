package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: place this to FragmentationMemberTest.java
class MemberMapperCollectionImplTest {
//    private static final String COLLECTION_NAME = "collection";
//    private final MemberMapper memberMapper = new MemberMapper("versionOfPath", "timestampPath");
//    private MemberMapperCollectionImpl memberMapperCollection;
//
//    @BeforeEach
//    void setUp() {
//        memberMapperCollection = new MemberMapperCollectionImpl();
//    }
//
//    @Test
//    void given_EmptyMemberMapperCollection_when_GetMemberMapper_then_ReturnEmptyOptional() {
//        Optional<MemberMapper> result = memberMapperCollection.getMemberMapper(COLLECTION_NAME);
//
//        assertThat(result).isEmpty();
//    }
//
//    @Test
//    void given_MemberMapperCollectionWithMemberMapper_when_GetMemberMapper_then_ReturnPresentOptional() {
//        memberMapperCollection.addMemberMapper(COLLECTION_NAME, memberMapper);
//
//        Optional<MemberMapper> result = memberMapperCollection.getMemberMapper(COLLECTION_NAME);
//
//        assertThat(result).contains(memberMapper);
//    }
//
//    @Test
//    void given_MemberMapperCollectionWithMemberMapper_when_DeleteMemberMapper_then_MemberMapperIsRemoved() {
//        memberMapperCollection.addMemberMapper(COLLECTION_NAME, memberMapper);
//
//        memberMapperCollection.deleteMemberMapper(COLLECTION_NAME);
//
//        Optional<MemberMapper> result = memberMapperCollection.getMemberMapper(COLLECTION_NAME);
//        assertThat(result).isEmpty();
//    }
//
//    @Test
//    void test_HandleEventStreamDeletedEvent() {
//        memberMapperCollection.addMemberMapper(COLLECTION_NAME, memberMapper);
//        assertThat(memberMapperCollection.getMemberMapper(COLLECTION_NAME)).isPresent();
//
//        memberMapperCollection.handleEventStreamDeletedEvent(new EventStreamDeletedEvent(this, COLLECTION_NAME));
//
//        assertThat(memberMapperCollection.getMemberMapper(COLLECTION_NAME)).isEmpty();
//    }
//
//    @Test
//    void test_HandleEventStreamCreatedEvent() {
//        final EventStream eventStream = new EventStream(COLLECTION_NAME, "timestampPath", "versionOfPath", false);
//
//        memberMapperCollection.handleEventStreamCreatedEvent(new EventStreamCreatedEvent(this, eventStream));
//
//        assertThat(memberMapperCollection.getMemberMapper(COLLECTION_NAME))
//                .hasValueSatisfying(actualMemberMapper -> assertThat(actualMemberMapper)
//                        .usingRecursiveComparison()
//                        .isEqualTo(memberMapper));
//    }
}
