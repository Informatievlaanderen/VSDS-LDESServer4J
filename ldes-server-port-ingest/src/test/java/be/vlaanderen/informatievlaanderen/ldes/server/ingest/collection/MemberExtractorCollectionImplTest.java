package be.vlaanderen.informatievlaanderen.ldes.server.ingest.collection;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor.VersionObjectMemberExtractor;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor.MemberExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class MemberExtractorCollectionImplTest {
    private static final String COLLECTION_NAME = "collection";
    private MemberExtractorCollection memberExtractorCollection;
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
    void test_Deletion() {
        assertThat(memberExtractorCollection.getMemberExtractor(COLLECTION_NAME))
                .isPresent();

        memberExtractorCollection.deleteMemberExtractor(COLLECTION_NAME);

        assertThat(memberExtractorCollection.getMemberExtractor(COLLECTION_NAME))
                .isEmpty();
    }
}