package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.exceptions;

public class DuplicateFragmentPairException extends RuntimeException {

    private final String fragmentId;
    private final String fragmentKey;

    public DuplicateFragmentPairException(String fragmentId, String fragmentKey) {
        this.fragmentId = fragmentId;
        this.fragmentKey = fragmentKey;
    }

    @Override
    public String getMessage() {
        return "FragmentId " + fragmentId + " already contains fragmentkey " + fragmentKey;
    }

}
