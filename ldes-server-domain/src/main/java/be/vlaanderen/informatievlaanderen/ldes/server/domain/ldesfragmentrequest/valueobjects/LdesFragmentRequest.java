package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public record LdesFragmentRequest(String collectionName, String viewName, List<FragmentPair> fragmentPairs) {

    public static LdesFragmentRequest createViewRequest(String collectionName, String viewName) {
        return new LdesFragmentRequest(collectionName, viewName, List.of());
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

    public String generateFragmentId() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("/").append(viewName);

        if (!fragmentPairs.isEmpty()) {
            stringBuilder.append("?");
            stringBuilder.append(
                    fragmentPairs
                            .stream()
                            .map(fragmentPair -> fragmentPair.fragmentKey() + "=" + fragmentPair.fragmentValue())
                            .collect(Collectors.joining("&"))
            );
        }

        return stringBuilder.toString();
    }

}
