package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.MemberIngester;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.validators.IngestValidator;
import io.micrometer.observation.annotation.Observed;
import org.apache.jena.rdf.model.Model;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Observed
@RestController
public class MemberIngestController implements OpenApiMemberIngestController {

	private final List<IngestValidator> validators;
	private final MemberIngester memberIngester;

	public MemberIngestController(List<IngestValidator> validators, MemberIngester memberIngester) {
		this.validators = validators;
        this.memberIngester = memberIngester;
	}

	@Override
	@PostMapping(value = "{collectionName}")
	public ResponseEntity<Object> ingestLdesMember(@RequestBody Model ingestedModel, @PathVariable String collectionName) {
		validators.forEach(ingestValidator -> ingestValidator.validate(ingestedModel, collectionName));
		HttpStatus statusCode = memberIngester.ingest(collectionName, ingestedModel) ? HttpStatus.CREATED : HttpStatus.OK;
		return new ResponseEntity<>(statusCode);
	}

}
