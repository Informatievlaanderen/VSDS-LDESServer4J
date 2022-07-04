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

    private static final String CACHE_CONTROL_HEADER = "Cache-Control";
    private static final String CACHE_CONTROL_IMMUTABLE = "public, max-age=604800, immutable";
    private static final String CACHE_CONTROL_MUTABLE = "public, max-age=60";

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
            return redirectToInitialFragment(response);
        } else {
            return returnRequestedFragment(response, value);
        }
    }

    private LdesFragment redirectToInitialFragment(HttpServletResponse response) throws IOException {
        LdesFragment initialFragment = fragmentationService.getInitialFragment(collectionName, viewConfig.getTimestampPath());
        setCacheControlHeader(response, initialFragment);
        if (initialFragment.isExistingFragment())
            response.sendRedirect(String.format("/%s?%s=%s", collectionName, viewConfig.getCompactTimestampPath(), initialFragment.getFragmentInfo().getValue()));
        return initialFragment;
    }

    private LdesFragment returnRequestedFragment(HttpServletResponse response, String value) {
        LdesFragment fragment = fragmentationService.getFragment(collectionName, viewConfig.getTimestampPath(), value);
        setCacheControlHeader(response, fragment);
        return fragment;
    }

    private void setCacheControlHeader(HttpServletResponse response, LdesFragment fragment) {
        if (fragment.isImmutable()) {
            response.setHeader(CACHE_CONTROL_HEADER, CACHE_CONTROL_IMMUTABLE);
        } else {
            response.setHeader(CACHE_CONTROL_HEADER, CACHE_CONTROL_MUTABLE);
        }
    }

}
