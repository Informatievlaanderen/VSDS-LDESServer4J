package be.vlaanderen.informatievlaanderen.ldes.server.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.FragmentProvider;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.converters.LdesFragmentConverter;
import org.apache.jena.riot.RDFFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

import static be.vlaanderen.informatievlaanderen.ldes.server.rest.converters.LdesFragmentConverter.outputLdesFragment;

@RestController
public class LdesFragmentController {

    private final FragmentProvider fragmentProvider;

    public LdesFragmentController(FragmentProvider fragmentProvider) {
        this.fragmentProvider = fragmentProvider;
    }

    @GetMapping(value = "/ldes-fragment")
    String retrieveLdesFragmentsPageAsJsonLd(HttpServletResponse response) {
        response.setContentType("application/ld+json");
        return outputLdesFragment(fragmentProvider.getFragment(), RDFFormat.JSONLD10_COMPACT_PRETTY);
    }

    @GetMapping(value = "/ldes-fragment", produces = "application/n-quads")
    String retrieveLdesFragmentsPageAsNQuads(HttpServletResponse response) {
        response.setContentType("application/n-quads");
        return outputLdesFragment(fragmentProvider.getFragment(), RDFFormat.NQUADS);
    }

}
