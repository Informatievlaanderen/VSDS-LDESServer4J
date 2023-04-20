package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.ShaclCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.LdesConfigShaclValidator;
import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/api/v1")
public class AdminShapeRestController {

	private final LdesConfigShaclValidator shapeValidator;
	private final ApplicationEventPublisher eventPublisher;
	private final ShaclCollection shaclCollection;

	public AdminShapeRestController(@Qualifier("shapeShaclValidator") LdesConfigShaclValidator shapeValidator,
			ApplicationEventPublisher eventPublisher, ShaclCollection shaclCollection) {
		this.shapeValidator = shapeValidator;
		this.eventPublisher = eventPublisher;
		this.shaclCollection = shaclCollection;
	}

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(shapeValidator);
	}

	@GetMapping("/eventstreams/{collectionName}/shape")
	public ResponseEntity<Model> getShape(@PathVariable String collectionName) {
		ShaclShape shape = shaclCollection.retrieveShape(collectionName);
		return ResponseEntity.ok(shape.getModel());
	}

	@PutMapping("/eventstreams/{collectionName}/shape")
	public void putShape(@PathVariable String collectionName,
			@RequestBody @Validated Model shape) {
		eventPublisher.publishEvent(new ShaclChangedEvent(new ShaclShape(collectionName, shape)));
		return ResponseEntity.ok(shape);
	}

}
