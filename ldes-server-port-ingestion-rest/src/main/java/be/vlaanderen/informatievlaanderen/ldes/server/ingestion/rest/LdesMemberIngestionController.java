package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.SdsReader;

@RestController
public class LdesMemberIngestionController {

    private final SdsReader sdsReader;

    public LdesMemberIngestionController(final SdsReader sdsReader) {
        this.sdsReader = sdsReader;
    }

    @PostMapping(value = "/ldes-member", consumes = "application/n-quads")
    public LdesMember ingestLdesMember(@RequestBody LdesMember ldesMember) {
        return sdsReader.storeLdesMember(ldesMember);
    }
}