package be.vlaanderen.informatievlaanderen.ldes.server.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.SdsReader;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class LdesFragmentController {

    private final SdsReader sdsReader;
    
    public LdesFragmentController(final SdsReader sdsReader) {
    	this.sdsReader = sdsReader;
    }
}
