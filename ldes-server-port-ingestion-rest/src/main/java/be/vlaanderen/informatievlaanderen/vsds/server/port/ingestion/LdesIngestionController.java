package be.vlaanderen.informatievlaanderen.vsds.server.port.ingestion;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converters.JenaConverter;

@RestController
public class LdesIngestionController {

    private final JenaConverter jenaConverter;

    public LdesIngestionController(JenaConverter jenaConverter) {
        this.jenaConverter = jenaConverter;
    }

    @PostMapping(value = "/ldes-fragment", consumes="application/n-quads")
    public void ingestLdesMember(@RequestBody String ldesMember) {
    	Model model = ModelFactory.createDefaultModel();
    	Lang lang = Lang.NQUADS;
    	
        this.jenaConverter.readModelFromString(ldesMember, model, lang);
        
        
    }

}
