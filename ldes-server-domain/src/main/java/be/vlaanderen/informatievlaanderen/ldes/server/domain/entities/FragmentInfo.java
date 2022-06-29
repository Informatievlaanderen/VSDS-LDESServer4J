package be.vlaanderen.informatievlaanderen.ldes.server.domain.entities;

public class FragmentInfo {
    private final String view;
    private final String shape;
    private final String viewShortName;
    private final String path;
    private String value;
    private Boolean immutable;

    public FragmentInfo(String view, String shape, String viewShortName, String path, String value) {
        this.view = view;
        this.shape = shape;
        this.viewShortName = viewShortName;
        this.path = path;
        this.value = value;
        this.immutable = false;
    }

    public String getPath() {
        return path;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getViewShortName() {
        return viewShortName;
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
