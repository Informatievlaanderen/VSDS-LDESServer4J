package be.vlaanderen.informatievlaanderen.ldes.server.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentview.entities.LdesFragmentView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentview.services.FragmentViewingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
public class LdesFragmentController {

    private static final String CACHE_CONTROL_HEADER = "Cache-Control";
    private static final String CACHE_CONTROL_IMMUTABLE = "public, max-age=604800, immutable";
    private static final String CACHE_CONTROL_MUTABLE = "public, max-age=60";

    private final FragmentViewingService fragmentationService;

    @Value("${ldes.collectionname}")
    private String collectionName;

    public LdesFragmentController(FragmentViewingService fragmentationService) {
        this.fragmentationService = fragmentationService;
    }


    @GetMapping(value = "${ldes.collectionname}", produces = {"application/ld+json", "application/n-quads"})
    LdesFragmentView retrieveLdesFragment(HttpServletResponse response, @RequestParam Map<String, String> requestParameters) throws IOException {
        if (requestParameters.isEmpty()) {
            return redirectToInitialFragment(response);
        } else {
            return returnRequestedFragment(response, requestParameters);
        }
    }

    private LdesFragmentView redirectToInitialFragment(HttpServletResponse response) throws IOException {
        LdesFragmentView initialFragment = fragmentationService.getInitialFragment(collectionName);
        setCacheControlHeader(response, initialFragment);
        //TODO
        if (initialFragment.getFragmentId().split(collectionName).length>1)
            response.sendRedirect(
                    initialFragment.getFragmentId().split("http://localhost:8080")[1]);
        return initialFragment;
    }

    private LdesFragmentView returnRequestedFragment(HttpServletResponse response, Map<String, String> fragmentationMap) {
        LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(collectionName, fragmentationMap.entrySet().stream().map(entry -> new FragmentPair(entry.getKey(), entry.getValue())).toList());
        LdesFragmentView fragment = fragmentationService.getFragment(ldesFragmentRequest);
        setCacheControlHeader(response, fragment);
        return fragment;
    }

    private void setCacheControlHeader(HttpServletResponse response, LdesFragmentView fragment) {
        if (fragment.isImmutable()) {
            response.setHeader(CACHE_CONTROL_HEADER, CACHE_CONTROL_IMMUTABLE);
        } else {
            response.setHeader(CACHE_CONTROL_HEADER, CACHE_CONTROL_MUTABLE);
        }
    }

}
