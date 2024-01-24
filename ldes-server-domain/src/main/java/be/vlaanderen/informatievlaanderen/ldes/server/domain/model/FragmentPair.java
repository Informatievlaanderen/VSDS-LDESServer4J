package be.vlaanderen.informatievlaanderen.ldes.server.domain.model;

import java.util.Objects;

public record FragmentPair(String fragmentKey, String fragmentValue) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FragmentPair that = (FragmentPair) o;
        return Objects.equals(fragmentKey, that.fragmentKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fragmentKey);
    }

}
