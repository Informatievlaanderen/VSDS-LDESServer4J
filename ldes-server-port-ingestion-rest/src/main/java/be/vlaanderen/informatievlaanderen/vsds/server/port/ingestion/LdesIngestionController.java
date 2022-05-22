package be.vlaanderen.informatievlaanderen.vsds.server.port.ingestion;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LdesIngestionController {

    private final FragmentProvider fragmentProvider;

    public LdesIngestionController(FragmentProvider fragmentProvider) {
        this.fragmentProvider = fragmentProvider;
    }

    @PostMapping(value = "/ldes-fragment", consumes="application/n-quads")
    public void ingestLdesMember(@RequestBody Contentype type, Model model) {
        response.setHeader("Content-Type", "application/ld+json; charset=utf-8");
        return fragmentProvider.getFragment();
    }

}
