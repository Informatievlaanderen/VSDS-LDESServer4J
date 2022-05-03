package be.vlaanderen.informatievlaanderen.ldes.server.port.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.LdesFragmentService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class LdesFragmentController {

    private final LdesFragmentService ldesFragmentService;

    @PostMapping(value = "/ldes-fragment", consumes = {MediaType.APPLICATION_JSON_VALUE})
    JSONObject createLdesFragment(@RequestBody JSONObject ldesMember) {
        return ldesFragmentService.storeLdesFragment(ldesMember);
    }
}
