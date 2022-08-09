package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects;

import java.util.List;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;

public class FragmentInfo {
	
    private final String view;
    private final String shape;
    private final String collectionName;
    private final List<FragmentPair> fragmentPairs;
    private Boolean immutable;

    public FragmentInfo(String view, String shape, String collectionName, List<FragmentPair> fragmentPairs) {
        this.view = view;
        this.shape = shape;
        this.collectionName = collectionName;
        this.fragmentPairs = fragmentPairs;
        this.immutable = false;
    }

    public String getPath() {
        return fragmentPairs.stream().map(FragmentPair::fragmentKey).findFirst().orElse(null);
    }

    public String getValue() {
        return fragmentPairs.stream().map(FragmentPair::fragmentValue).findFirst().orElse(null);
    }

    public List<FragmentPair> getFragmentPairs() {
        return fragmentPairs;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public Boolean getImmutable() {
        return immutable;
    }

    public void setImmutable(Boolean immutable) {
        this.immutable = immutable;
    }

    public String getView() {
        return view;
    }

    public String getShape() {
        return shape;
    }
}
