package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import org.springframework.stereotype.Component;

@Component
public class FragmentProviderImpl implements FragmentProvider {
    private final FragmentCreator fragmentCreator;

    public FragmentProviderImpl(FragmentCreator fragmentCreator) {
        this.fragmentCreator = fragmentCreator;
    }

    @Override
    public LdesFragment getFragment() {
        return fragmentCreator.createFragment();
    }
}
