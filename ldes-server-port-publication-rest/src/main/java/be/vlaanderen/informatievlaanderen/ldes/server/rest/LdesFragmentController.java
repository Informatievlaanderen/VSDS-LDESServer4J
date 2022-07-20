package be.vlaanderen.informatievlaanderen.ldes.server.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.LdesFragmentRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class LdesFragmentController {

    private static final String CACHE_CONTROL_HEADER = "Cache-Control";
    private static final String CACHE_CONTROL_IMMUTABLE = "public, max-age=604800, immutable";
    private static final String CACHE_CONTROL_MUTABLE = "public, max-age=60";

    private final FragmentationService fragmentationService;

    @Value("${ldes.collectionname}")
    private String collectionName;

    public LdesFragmentController(FragmentationService fragmentationService) {
        this.fragmentationService = fragmentationService;
    }


    @GetMapping(value = "${ldes.collectionname}", produces = {"application/ld+json", "application/n-quads"})
    LdesFragment retrieveLdesFragment(HttpServletResponse response, @RequestParam Map<String, String> requestParameters) throws IOException {
        if (requestParameters.isEmpty()) {
            return redirectToInitialFragment(response);
        } else {
            return returnRequestedFragment(response, requestParameters);
        }
    }

    private LdesFragment redirectToInitialFragment(HttpServletResponse response) throws IOException {
        LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(collectionName, List.of());
        LdesFragment initialFragment = fragmentationService.getInitialFragment(ldesFragmentRequest);
        setCacheControlHeader(response, initialFragment);
        if (initialFragment.isExistingFragment())
            response.sendRedirect("/" + collectionName + "?" + initialFragment.getFragmentInfo().getPath() + "=" + initialFragment.getFragmentInfo().getValue());
        return initialFragment;
    }

    private LdesFragment returnRequestedFragment(HttpServletResponse response, Map<String, String> fragmentationMap) {
        LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(collectionName, fragmentationMap.entrySet().stream().map(entry -> new FragmentPair(entry.getKey(), entry.getValue())).toList());
        LdesFragment fragment = fragmentationService.getFragment(ldesFragmentRequest);
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
