package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesFragmentConfig;
import org.springframework.stereotype.Component;

@Component
public class FragmentProviderImpl implements FragmentProvider {
    private final LdesFragmentConfig ldesFragmentConfig;

    private final FragmentCreator fragmentCreator;

    public FragmentProviderImpl(FragmentCreator fragmentCreator, LdesFragmentConfig ldesFragmentConfig) {
        this.fragmentCreator = fragmentCreator;
        this.ldesFragmentConfig = ldesFragmentConfig;
    }

    @Override
    public LdesFragment getFragment() {
        return fragmentCreator.createFragment(ldesFragmentConfig.toMap());
    }
}
