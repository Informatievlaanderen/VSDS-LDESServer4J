package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CompactionCandidateSorter {
    private CompactionCandidateSorter() {
    }

    public static Collection<Set<CompactionCandidate>> getCompactionCandidateList(List<CompactionCandidate> candidatesList, int capacity) {

        List<CompactionCandidate> firstElements = candidatesList.stream()
                .filter(candidate -> hasNoConnections(candidate, candidatesList.stream()
                        .toList())).toList();

        AtomicInteger index = new AtomicInteger();
        Map<Integer, Set<CompactionCandidate>> compactionDesign = new HashMap<>();

        firstElements.forEach(candidate -> {
            int currentCapacity = 0;
            Set<CompactionCandidate> splitList = new HashSet<>();
            Optional<CompactionCandidate> currentCandidate = Optional.of(candidate);
            while (currentCandidate.isPresent()) {
                currentCapacity += currentCandidate.get().getSize();
                if (currentCapacity > capacity) {
                    currentCapacity = 0;
                    if(splitList.size() > 1) {
                        compactionDesign.put(index.incrementAndGet(), splitList);
                        splitList = new HashSet<>();
                    }
                }
                else {
                    splitList.add(currentCandidate.get());
                }
                long nextId = currentCandidate.get().getNextPageId();
                currentCandidate = candidatesList.stream().filter(c->c.getId() == nextId).findFirst();
            }

            if(splitList.size() > 1) {
                compactionDesign.put(index.incrementAndGet(), splitList);
            }
        });

        return compactionDesign.values();
    }

    public static boolean hasNoConnections(CompactionCandidate fragment, List<CompactionCandidate> fragments) {
        return fragments.stream().noneMatch(fragment1 -> isConnectedTo(fragment1, fragment));
    }

    public static boolean isConnectedTo(CompactionCandidate treeNode, CompactionCandidate otherTreeNode) {
        return treeNode.getNextPageId() == otherTreeNode.getId();
    }
}
