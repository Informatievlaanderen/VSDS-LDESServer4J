package be.vlaanderen.informatievlaanderen.ldes.server.rest;

import org.springframework.http.MediaType;
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

    @PostMapping(value = "/ldes-member", consumes = MediaType.TEXT_PLAIN_VALUE)
    public void ingestLdesMember(@RequestBody String ldesMemberData) {
        LdesMember ldesMember = new LdesMember(ldesMemberData.split("\n"));

        this.sdsReader.storeLdesMember(ldesMember);
    }
}