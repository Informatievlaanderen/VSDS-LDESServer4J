package be.vlaanderen.informatievlaanderen.ldes.server.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.SdsReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class LdesFragmentController {

    private final SdsReader sdsReader;
    
    @Autowired
    public LdesFragmentController(final SdsReader sdsReader) {
    	this.sdsReader = sdsReader;
    }
}
