package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentationConfig;

import java.util.Map;

public class FragmentationConfigEntity {
    private String name;
    private Map<String, String> config;

    public FragmentationConfigEntity() {}

    public FragmentationConfigEntity(String name, Map<String, String> config) {
        this.name = name;
        this.config = config;
    }

    public static FragmentationConfigEntity toEntity(FragmentationConfig fragmentationConfig) {
        return new FragmentationConfigEntity(fragmentationConfig.getName(), fragmentationConfig.getConfig());
    }

    public FragmentationConfig fromEntity() {
        var fragmentationConfig = new FragmentationConfig();
        fragmentationConfig.setName(this.name);
        fragmentationConfig.setConfig(this.config);
        return fragmentationConfig;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }

    //TODO: add equals and hashCode
}
