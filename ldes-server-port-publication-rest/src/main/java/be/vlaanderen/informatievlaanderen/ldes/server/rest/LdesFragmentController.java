package be.vlaanderen.informatievlaanderen.ldes.server.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.ViewConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.FragmentationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LdesFragmentController {

    private final FragmentationService fragmentationService;
    private final ViewConfig viewConfig;
    @Value("${ldes.collectionname}")
    private String collectionName;

    public LdesFragmentController(FragmentationService fragmentationService, ViewConfig viewConfig) {
        this.fragmentationService = fragmentationService;
        this.viewConfig = viewConfig;
    }

    @GetMapping(value = "${ldes.collectionname}", produces = { "application/ld+json", "application/n-quads" })
    LdesFragment retrieveLdesFragment(
                                      @RequestParam(name = "generatedAtTime") String value) {
        return fragmentationService.getFragment(collectionName, viewConfig.getTimestampPath(), value);
    }

}
