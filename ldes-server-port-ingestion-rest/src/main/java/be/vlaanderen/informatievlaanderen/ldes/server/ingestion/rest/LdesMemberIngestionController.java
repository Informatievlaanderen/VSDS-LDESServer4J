package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.FragmentationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;

@RestController
public class LdesMemberIngestionController {

    private final FragmentationService ldesReader;

    public LdesMemberIngestionController(final FragmentationService ldesReader) {
        this.ldesReader = ldesReader;
    }

    @PostMapping(value = "/ldes-member", consumes = "application/n-quads")
    public LdesMember ingestLdesMember(@RequestBody LdesMember ldesMember) {
        return ldesReader.addMember(ldesMember);
    }
}