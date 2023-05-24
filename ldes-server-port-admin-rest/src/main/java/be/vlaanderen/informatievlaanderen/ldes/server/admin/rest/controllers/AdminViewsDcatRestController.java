package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.DcatViewValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.ViewValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewSpecificationConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.apache.jena.riot.WebContent.*;

@RestController
@RequestMapping("/admin/api/v1/eventstreams/{collectionName}/views/{viewName}/dcat")
@Tag(name = "Views DCAT")
public class AdminViewsDcatRestController {

	// TODO: 23/05/2023 dcat service
	private final ViewService viewService;

	private final DcatViewValidator dcatViewValidator;

	// TODO: 23/05/2023 check this
	private final ViewSpecificationConverter viewConverter;

	public AdminViewsDcatRestController(ViewService viewService, DcatViewValidator dcatViewValidator,
                                        ViewSpecificationConverter viewConverter) {
		this.viewService = viewService;
		this.dcatViewValidator = dcatViewValidator;
		this.viewConverter = viewConverter;
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
		// TODO: 23/05/2023 validate some basic stuff
		// TODO: 23/05/2023 call service to store bla
//		viewService.addView(viewConverter.viewFromModel(view, collectionName));
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
		// TODO: 23/05/2023 validate some basic stuff
		// TODO: 23/05/2023 call service to store bla
//		viewService.addView(viewConverter.viewFromModel(view, collectionName));
	}

	@DeleteMapping("/{viewName}")
	@Operation(summary = "Delete dcat metadata for a view")
	public void deleteView(@PathVariable String collectionName, @PathVariable String viewName) {
		// TODO: 23/05/2023 call
	}

}
