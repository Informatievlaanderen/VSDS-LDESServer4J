package be.vlaanderen.informatievlaanderen.ldes.server.fetching.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier.fromFragmentId;
import static be.vlaanderen.informatievlaanderen.ldes.server.fetching.services.CompactionCandidateSorter.sortCompactionCandidates;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CompactionCandidateSorterTest {

    Fragment f5 = new Fragment(fromFragmentId("/collection/5"), true, 0,
            List.of(), null);
    Fragment f4 = new Fragment(fromFragmentId("/collection/4"), true, 0,
            List.of(), null);
    Fragment f3 = new Fragment(fromFragmentId("/collection/3"), true, 0,
            List.of(new TreeRelation(null, f4.getFragmentId(), null, null, null)),
            null);
    Fragment f2 = new Fragment(fromFragmentId("/collection/2"), true, 0,
            List.of(new TreeRelation(null, f3.getFragmentId(), null, null, null)),
            null);
    Fragment f1 = new Fragment(fromFragmentId("/collection/1"), true, 0,
            List.of(new TreeRelation(null, f2.getFragmentId(), null, null, null)),
            null);

    List<CompactionCandidate> expectedOrderedList = List.of(deriveCandidate(f1), deriveCandidate(f2), deriveCandidate(f3), deriveCandidate(f4));


    @Test
    void given_OrderedList_when_sortCandidates_then_ReturnSameOrderedList() {
        var sorted = CompactionCandidateSorter.sortCompactionCandidates(
                        Stream.of(deriveCandidate(f1), deriveCandidate(f2), deriveCandidate(f3), deriveCandidate(f4)))
                .toList();

        assertThat(sorted).containsExactlyInAnyOrderElementsOf(expectedOrderedList);
    }

    @Test
    void given_ReversedOrderedList_when_sortCandidates_then_ReturnCorrectlyOrderedList() {
        var sorted = CompactionCandidateSorter.sortCompactionCandidates(
                        Stream.of(deriveCandidate(f4), deriveCandidate(f3), deriveCandidate(f2), deriveCandidate(f1)))
                .toList();

        assertThat(sorted).containsExactlyInAnyOrderElementsOf(expectedOrderedList);
    }

    @Test
    void given_ChaoticallyOrderedList_when_sortCandidates_then_ThrowException() {
        final List<CompactionCandidate> compactionCandidates = List.of(
                deriveCandidate(f3), deriveCandidate(f5), deriveCandidate(f1), deriveCandidate(f4), deriveCandidate(f2));
        final Stream<CompactionCandidate> compactionCandidateStream = compactionCandidates.stream();

        assertThatThrownBy(() -> sortCompactionCandidates(compactionCandidateStream))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Not all compaction candidates are linked. Candidates: " + compactionCandidates);
    }

    @Test
    void given_NoCandidates_when_SortCandidates_then_ReturnEmptyStream() {
        var sorted = CompactionCandidateSorter.sortCompactionCandidates(Stream.of()).toList();

        assertThat(sorted).isEmpty();
    }

    private CompactionCandidate deriveCandidate(Fragment fragment) {
        var cc = new CompactionCandidate(fragment.getFragmentIdString(), 0);
        cc.setFragment(fragment);
        return cc;
    }
}