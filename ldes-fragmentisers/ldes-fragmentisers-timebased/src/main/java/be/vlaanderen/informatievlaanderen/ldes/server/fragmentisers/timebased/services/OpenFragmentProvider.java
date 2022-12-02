package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OpenFragmentProvider {

    private final TimeBasedFragmentCreator fragmentCreator;
    private final LdesFragmentRepository ldesFragmentRepository;
    private final ExecutorService executors;

    public OpenFragmentProvider(TimeBasedFragmentCreator fragmentCreator,
                                LdesFragmentRepository ldesFragmentRepository) {
        this.fragmentCreator = fragmentCreator;
        this.ldesFragmentRepository = ldesFragmentRepository;
        this.executors = Executors.newSingleThreadExecutor();
    }

    public LdesFragment retrieveOpenFragmentOrCreateNewFragment(LdesFragment parentFragment) {
        return ldesFragmentRepository
                .retrieveOpenChildFragment(parentFragment.getFragmentInfo().getViewName(),
                        parentFragment.getFragmentInfo().getFragmentPairs())
                .map(fragment -> {
                    if (fragmentCreator.needsToCreateNewFragment(fragment)) {
                        LdesFragment newFragment = fragmentCreator.createNewFragment(fragment, parentFragment);
                        executors.submit(() -> ldesFragmentRepository.saveFragment(newFragment));
                        return newFragment;
                    } else {
                        return fragment;
                    }
                })
                .orElseGet(() -> {
                    LdesFragment newFragment = fragmentCreator.createNewFragment(parentFragment);
                    executors.submit(() -> ldesFragmentRepository.saveFragment(newFragment));
                    return newFragment;
                });
    }
}
