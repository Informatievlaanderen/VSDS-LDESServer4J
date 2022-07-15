package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities;

import java.util.List;
import java.util.Objects;

public record LdesFragmentRequest(String collectionName, List<FragmentPair> fragmentPairs) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LdesFragmentRequest that = (LdesFragmentRequest) o;
        return Objects.equals(collectionName, that.collectionName) && Objects.equals(fragmentPairs, that.fragmentPairs);
    }

}
