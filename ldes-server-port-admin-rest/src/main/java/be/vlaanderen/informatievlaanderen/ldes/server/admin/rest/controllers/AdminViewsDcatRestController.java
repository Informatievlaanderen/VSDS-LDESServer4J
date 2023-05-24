package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.DcatViewValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.DcatViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.jena.rdf.model.Model;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import static org.apache.jena.riot.WebContent.contentTypeJSONLD;
import static org.apache.jena.riot.WebContent.contentTypeNQuads;
import static org.apache.jena.riot.WebContent.contentTypeTurtle;

@RestController
@RequestMapping("/admin/api/v1/eventstreams/{collectionName}/views/{viewName}/dcat")
@Tag(name = "Views DCAT")
public class AdminViewsDcatRestController {

	private final DcatViewService dcatViewService;

	private final DcatViewValidator dcatViewValidator;

	public AdminViewsDcatRestController(DcatViewService dcatViewService, DcatViewValidator dcatViewValidator) {
		this.dcatViewService = dcatViewService;
		this.dcatViewValidator = dcatViewValidator;
	}

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(dcatViewValidator);
	}

	@PostMapping(consumes = { contentTypeJSONLD, contentTypeNQuads,
			contentTypeTurtle })
	@Operation(summary = "Add DCAT metadata for a view")
	@ResponseStatus(HttpStatus.CREATED)
	public void createDcat(@PathVariable String collectionName, @PathVariable String viewName,
			@Parameter(schema = @Schema(implementation = String.class),
					description = "a blank node of type dcat:DataService with only dcat:DataService properties " +
							"(e.g. dct:title) and relations (e.g. dct:license), excluding relations " +
							"(non-configurable metadata) to dcat:Catalog and dcat:DataService",
			example = """
					@prefix dct:   <http://purl.org/dc/terms/> .
					@prefix dcat:  <http://www.w3.org/ns/dcat#> .
					     
					[] a dcat:DataService ;
					  dct:title "My geo-spatial view"@en ;
					  dct:description "Geospatial fragmentation for my LDES"@en .
					""") @RequestBody @Validated Model dcat) {
		dcatViewService.create(new ViewName(collectionName, viewName), dcat);
	}

	@PutMapping(consumes = { contentTypeJSONLD, contentTypeNQuads, contentTypeTurtle })
	@Operation(summary = "Update DCAT metadata for a view")
	public void updateDcat(@PathVariable String collectionName, @PathVariable String viewName,
			@Parameter(schema = @Schema(implementation = String.class),
					description = "a blank node of type dcat:DataService with only dcat:DataService properties " +
							"(e.g. dct:title) and relations (e.g. dct:license), excluding relations " +
							"(non-configurable metadata) to dcat:Catalog and dcat:DataService",
			example = """
					@prefix dct:   <http://purl.org/dc/terms/> .
					@prefix dcat:  <http://www.w3.org/ns/dcat#> .
					@prefix foaf:  <http://xmlns.com/foaf/0.1/> .
					@prefix org:   <http://www.w3.org/ns/org#> .
					@prefix legal: <http://www.w3.org/ns/legal#> .
					@prefix m8g:   <http://data.europa.eu/m8g/>
					@prefix locn:  <http://www.w3.org/ns/locn#>
					     
					[] a dcat:DataService ;
					  dct:title "My geo-spatial view"@en ;
					  dct:description "Geospatial fragmentation for my LDES"@en ;
					  dct:license [
					    a dct:LicenseDocument ;
					    dct:type [
					      rdf:type skos:Concept;
					      skos:prefLabel "some public license"@en
					    ]
					  ] .
					""") @RequestBody @Validated Model dcat) {
		dcatViewService.update(new ViewName(collectionName, viewName), dcat);
	}

	@DeleteMapping
	@Operation(summary = "Delete dcat metadata for a view")
	public void deleteDcat(@PathVariable String collectionName, @PathVariable String viewName) {
		dcatViewService.remove(new ViewName(collectionName, viewName));
	}

}
