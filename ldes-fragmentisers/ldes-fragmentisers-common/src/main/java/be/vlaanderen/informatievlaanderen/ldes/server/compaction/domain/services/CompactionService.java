package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.services;


import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.ViewCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CompactionService {
    private final ViewCollection viewCollection;
    private final FragmentRepository fragmentRepository;

    public CompactionService(ViewCollection viewCollection, FragmentRepository fragmentRepository) {
        this.viewCollection = viewCollection;
        this.fragmentRepository = fragmentRepository;
    }

    @Scheduled(fixedDelay = 10000)
    public void compactFragments() {
        viewCollection.getAllViewCapacities()
                .forEach(viewCapacity -> {
                    Optional<Fragment> fragment = fragmentRepository.retrieveRootFragment(viewCapacity.getViewName().asString());
                   if(fragment.isPresent()){
                       List<Fragment> startingNodes = getStartingNodes(fragment.get());
                       startingNodes.forEach(fragment1 -> {
                           System.out.println(fragment1.getFragmentId().asString());
                       });
                   }
                });

    }

    // Iterator
    private List<Fragment> getStartingNodes(Fragment fragment) {
        List<Fragment> startingNodes = new ArrayList<>();
        fragment.getRelations()
                .stream()
                .map(TreeRelation::treeNode)
                .map(fragmentRepository::retrieveFragment)
                .flatMap(Optional::stream)
                .forEach(relationFragment->{
                    if(relationFragment.getNumberOfMembers()>0 && relationFragment.isImmutable()){
                        startingNodes.add(relationFragment);
                    }else {
                        startingNodes.addAll(getStartingNodes(relationFragment));
                    }
                });
        return startingNodes;
    }
}
