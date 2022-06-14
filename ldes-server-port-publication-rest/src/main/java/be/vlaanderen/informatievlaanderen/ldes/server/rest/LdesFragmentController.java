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

    @GetMapping(value = "/ldes-fragment", produces = "application/ld+json")
    String retrieveLdesFragmentsPageAsJsonLd(HttpServletResponse response) {
        return outputLdesFragment(fragmentProvider.getFragment(), RDFFormat.JSONLD11);
    }

    @GetMapping(value = "/ldes-fragment", produces = "application/n-quads")
    String retrieveLdesFragmentsPageAsNQuads(HttpServletResponse response) {
        return outputLdesFragment(fragmentProvider.getFragment(), RDFFormat.NQUADS);
    }

}
