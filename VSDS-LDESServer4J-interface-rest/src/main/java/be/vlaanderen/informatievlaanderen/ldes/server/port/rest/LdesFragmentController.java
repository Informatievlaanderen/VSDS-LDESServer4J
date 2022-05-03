package be.vlaanderen.informatievlaanderen.ldes.server.port.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.LdesFragmentService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class LdesFragmentController {

    private final LdesFragmentService ldesFragmentService;

    @GetMapping("/ldes-fragment")
    JSONObject retrieveLdesFragmentsPage(@RequestParam(defaultValue = "0") int page) {
        return ldesFragmentService.retrieveLdesFragmentsPage(page);
    }

    @PostMapping(value = "/ldes-fragment", consumes = {MediaType.APPLICATION_JSON_VALUE})
    JSONObject createLdesFragment(@RequestBody JSONObject ldesMember) {
        return ldesFragmentService.storeLdesFragment(ldesMember);
    }


}
