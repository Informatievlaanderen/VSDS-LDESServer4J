package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.LdesReader;

@RestController
public class LdesMemberIngestionController {

    private final LdesReader ldesReader;

    public LdesMemberIngestionController(final LdesReader ldesReader) {
        this.ldesReader = ldesReader;
    }

    @PostMapping(value = "/ldes-member", consumes = "application/n-quads")
    public LdesMember ingestLdesMember(@RequestBody LdesMember ldesMember) {
        return ldesReader.storeLdesMember(ldesMember);
    }
}