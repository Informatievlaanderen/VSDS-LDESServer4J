package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.exceptions;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragment;

import java.util.List;

public class NoClosestFragmentFoundException extends RuntimeException {
    private final List<String> fragmentCollectionIds;
    private final String fragmentId;

    public NoClosestFragmentFoundException(LdesFragment ldesFragment, List<LdesFragment> fragmentCollection) {
        this.fragmentId = ldesFragment.getFragmentId();
        this.fragmentCollectionIds = fragmentCollection.stream()
                .map(LdesFragment::getFragmentId).toList();
    }

    @Override
    public String getMessage() {
        return "Could not find closest fragment to fragment " +
                fragmentId +
                " in collection " +
                String.join(",", fragmentCollectionIds);
    }
}
