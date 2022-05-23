package be.vlaanderen.informatievlaanderen.vsds.server.port.ingestion;

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

    @PostMapping(value = "/ldes-fragment", consumes="application/n-quads")
    public void ingestLdesMember(@RequestBody String ldesMemberData) {
        LdesMember ldesMember = new LdesMember(ldesMemberData.split("\n"));
        
        this.sdsReader.storeLdesMember(ldesMember);
    }
}
