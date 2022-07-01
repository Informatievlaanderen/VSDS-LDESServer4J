package be.vlaanderen.informatievlaanderen.ldes.server.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.ViewConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.FragmentationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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


    @GetMapping(value = "${ldes.collectionname}", produces = {"application/ld+json", "application/n-quads"})
    LdesFragment retrieveLdesFragment(HttpServletResponse response, @RequestParam(name = "generatedAtTime", required = false) String value) throws IOException {
        if (value == null) {
            return returnInitialFragment(response);
        } else {
            if(value.equals("null"))
                value=null;
            return returnRequestedFragment(response, value);
        }
    }

    private LdesFragment returnRequestedFragment(HttpServletResponse response, String value) {
        LdesFragment fragment = fragmentationService.getFragment(collectionName, viewConfig.getTimestampPath(), value);
        setCacheControlHeader(response, fragment);
        return fragment;
    }

    private LdesFragment returnInitialFragment(HttpServletResponse response) throws IOException {
        LdesFragment fragment = fragmentationService.getInitialFragment(collectionName, viewConfig.getTimestampPath());
        response.sendRedirect(String.format("/%s?%s=%s", collectionName, viewConfig.getCompactTimestampPath(), fragment.getFragmentInfo().getValue()));
        return null;
    }

    private void setCacheControlHeader(HttpServletResponse response, LdesFragment fragment) {
        if (fragment.isImmutable()) {
            response.setHeader("Cache-Control", "public, max-age=604800, immutable");
        }
    }

}
