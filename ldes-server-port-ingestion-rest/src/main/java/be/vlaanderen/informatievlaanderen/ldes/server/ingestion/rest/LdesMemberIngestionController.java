package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;

@RestController
public class LdesMemberIngestionController {

    private final FragmentationService ldesReader;

    public LdesMemberIngestionController(final FragmentationService ldesReader) {
        this.ldesReader = ldesReader;
    }

    @PostMapping(value = "${ldes.collectionname}", consumes = {"application/n-quads", "application/n-triples"})
    public LdesMember ingestLdesMember(@RequestBody LdesMember ldesMember) {
        return ldesReader.addMember(ldesMember);
    }
}