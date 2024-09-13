package be.vlaanderen.informatievlaanderen.ldes.server.fetching.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;

import java.util.List;
import java.util.Objects;

public record LdesFragmentRequest(ViewName viewName, List<FragmentPair> fragmentPairs) {

    public static LdesFragmentRequest createViewRequest(ViewName viewName) {
        return new LdesFragmentRequest(viewName, List.of());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else {
            if (o == null || getClass() != o.getClass()) {
                return false;
            } else {
                LdesFragmentRequest that = (LdesFragmentRequest) o;
                return Objects.equals(viewName, that.viewName) && Objects.equals(fragmentPairs, that.fragmentPairs);
            }
        }
    }

}
