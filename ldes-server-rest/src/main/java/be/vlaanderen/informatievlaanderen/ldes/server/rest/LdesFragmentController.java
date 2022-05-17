package be.vlaanderen.informatievlaanderen.ldes.server.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.SdsReader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class LdesFragmentController {

    private final SdsReader sdsReader;

}
