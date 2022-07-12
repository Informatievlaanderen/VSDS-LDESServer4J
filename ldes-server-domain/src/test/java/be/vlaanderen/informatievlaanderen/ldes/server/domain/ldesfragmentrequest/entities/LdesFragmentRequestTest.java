package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class LdesFragmentRequestTest {


    @Test
    @DisplayName("Test Equality of LdesFragmentRequest")
    void test_EqualityOfLdesFragmentRequests() {
        LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest("collectionName", List.of(new FragmentPair("key", "value"), new FragmentPair("key2", "value2")));
        LdesFragmentRequest otherLdesFragmentRequest = new LdesFragmentRequest("collectionName", List.of(new FragmentPair("key", "value"), new FragmentPair("key2", "value2")));
        assertEquals(ldesFragmentRequest, otherLdesFragmentRequest);
        assertEquals(ldesFragmentRequest, ldesFragmentRequest);
        assertEquals(otherLdesFragmentRequest, otherLdesFragmentRequest);
    }

    @ParameterizedTest
    @ArgumentsSource(LdesFragmentRequestArgumentsProvider.class)
    void test_InequalityOfLdesFragmentRequests(Object otherLdesFragmentRequest) {
        LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest("collectionName", List.of(new FragmentPair("key", "value"), new FragmentPair("key2", "value2")));
        assertNotEquals(ldesFragmentRequest, otherLdesFragmentRequest);
    }

    static class LdesFragmentRequestArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(new LdesMember(null)),
                    Arguments.of((Object) null),
                    Arguments.of(new LdesFragmentRequest("otherCollectionName", List.of(new FragmentPair("key", "value"), new FragmentPair("key2", "value2")))),
                    Arguments.of(new LdesFragmentRequest("collectionName", List.of(new FragmentPair("key2", "value2"), new FragmentPair("key", "value")))));
        }
    }

}