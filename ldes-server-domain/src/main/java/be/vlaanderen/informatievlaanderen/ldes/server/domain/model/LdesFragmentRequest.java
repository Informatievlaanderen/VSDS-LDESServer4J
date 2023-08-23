package be.vlaanderen.informatievlaanderen.ldes.server.domain.model;

import java.util.List;
import java.util.Objects;

public record LdesFragmentRequest(ViewName viewName, List<FragmentPair> fragmentPairs) {

    public static LdesFragmentRequest createViewRequest(ViewName viewName) {
        return new LdesFragmentRequest(viewName, List.of());
    }

    // @formatter:off
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
    // @formatter:on

}
