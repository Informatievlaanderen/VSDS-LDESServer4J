package be.vlaanderen.informatievlaanderen.ldes.server.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.FragmentProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class LdesFragmentController {

    private final FragmentProvider fragmentProvider;

    public LdesFragmentController(FragmentProvider fragmentProvider) {
        this.fragmentProvider = fragmentProvider;
    }

    @GetMapping(value = "/ldes-fragment")
    LdesFragment retrieveLdesFragmentsPage(HttpServletResponse response) {
        response.setHeader("Content-Type", "application/ld+json; charset=utf-8");
        return fragmentProvider.getFragment();
    }

}
