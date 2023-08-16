package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.jena.rdf.model.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import static org.apache.jena.riot.WebContent.contentTypeJSONLD;
import static org.apache.jena.riot.WebContent.contentTypeNQuads;
import static org.apache.jena.riot.WebContent.contentTypeTextPlain;
import static org.apache.jena.riot.WebContent.contentTypeTurtle;

@SuppressWarnings("java:S2479") // whitespace needed for examples
@Tag(name = "DCAT")
public interface OpenApiServerDcatController {

	@ApiResponse(responseCode = "200", description = "The combined DCAT of the server is returned", content = {
			@Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = Model.class), examples = {
					@ExampleObject(value = """
							@base <http://localhost:8080> .
							@prefix dct: <http://purl.org/dc/terms/> .
							@prefix dcat: <http://www.w3.org/ns/dcat#> .
							@prefix foaf: <http://xmlns.com/foaf/0.1/> .
							@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
							@prefix skos: <http://www.w3.org/2004/02/skos/core#> .
							@prefix legal: <http://www.w3.org/ns/legal#> .
							@prefix m8g:   <http://data.europa.eu/m8g/> .
							@prefix locn:  <http://www.w3.org/ns/locn#> .

							</> a dcat:Catalog ;
							  dct:title "My LDES'es"@en ;
							  dct:description "All LDES'es from publiser X"@en ;
							  dct:publisher <http://sample.org/company/PublisherX> ;
							  dcat:dataset </parcels> ;
							  dcat:service </parcels/by-page/description>.

							<http://sample.org/company/PublisherX> a foaf:Agent ;
							  foaf:name "Data Publishing Company" ;
							  legal:legalName "Data Publishing Company BV" ;
							  m8g:registeredAddress [
								a locn:Address ;
								locn:fullAddress "Some full address here"
							  ] ;
							  m8g:contactPoint [
								a m8g:ContactPoint ;
								m8g:hasEmail "info@data-publishing-company.com"
							  ] .

							</parcels> a dcat:Dataset ;
							 dct:title "My LDES"@en ;
							 dct:description "LDES for my data collection"@en .

							</parcels/by-page/description> a dcat:DataService ;
							  dcat:endpointURL </parcels/by-page> ;
							  dcat:servesDataset </parcels> ;
							  dct:title "My geo-spatial view"@en ;
							  dct:description "Geospatial fragmentation for my LDES"@en ;
							  dct:license [
								a dct:LicenseDocument ;
								dct:type [
								  rdf:type skos:Concept;
								  skos:prefLabel "some public license"@en
								]
							  ] .
									""")
			}),
			@Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = {
					@ExampleObject(value = """
							<http://localhost:8080/> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/dcat#Catalog> .
							<http://localhost:8080/> <http://purl.org/dc/terms/title> "My LDES'es"@en .
							<http://localhost:8080/> <http://purl.org/dc/terms/description> "All LDES'es from publiser X"@en .
							<http://localhost:8080/> <http://purl.org/dc/terms/publisher> <http://sample.org/company/PublisherX> .
							<http://localhost:8080/> <http://www.w3.org/ns/dcat#dataset> <http://localhost:8080/parcels> .
							<http://localhost:8080/> <http://www.w3.org/ns/dcat#service> <http://localhost:8080/parcels/by-page/description> .
							<http://sample.org/company/PublisherX> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Agent> .
							<http://sample.org/company/PublisherX> <http://xmlns.com/foaf/0.1/name> "Data Publishing Company" .
							<http://sample.org/company/PublisherX> <http://www.w3.org/ns/legal#legalName> "Data Publishing Company BV" .
							<http://sample.org/company/PublisherX> <http://data.europa.eu/m8g/registeredAddress> _:genid1 .
							<http://sample.org/company/PublisherX> <http://data.europa.eu/m8g/contactPoint> _:genid2 .
							_:genid1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/locn#Address> .
							_:genid1 <http://www.w3.org/ns/locn#fullAddress> "Some full address here" .
							_:genid2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://data.europa.eu/m8g/ContactPoint> .
							_:genid2 <http://data.europa.eu/m8g/hasEmail> "info@data-publishing-company.com" .
							<http://localhost:8080/parcels> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/dcat#Dataset> .
							<http://localhost:8080/parcels> <http://purl.org/dc/terms/title> "My LDES"@en .
							<http://localhost:8080/parcels> <http://purl.org/dc/terms/description> "LDES for my data collection"@en .
							<http://localhost:8080/parcels/by-page/description> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/dcat#DataService> .
							<http://localhost:8080/parcels/by-page/description> <http://www.w3.org/ns/dcat#endpointURL> <http://localhost:8080/parcels/by-page> .
							<http://localhost:8080/parcels/by-page/description> <http://www.w3.org/ns/dcat#servesDataset> <http://localhost:8080/parcels> .
							<http://localhost:8080/parcels/by-page/description> <http://purl.org/dc/terms/title> "My geo-spatial view"@en .
							<http://localhost:8080/parcels/by-page/description> <http://purl.org/dc/terms/description> "Geospatial fragmentation for my LDES"@en .
							<http://localhost:8080/parcels/by-page/description> <http://purl.org/dc/terms/license> _:genid3 .
							_:genid3 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/dc/terms/LicenseDocument> .
							_:genid3 <http://purl.org/dc/terms/type> _:genid4 .
							_:genid4 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2004/02/skos/core#Concept> .
							_:genid4 <http://www.w3.org/2004/02/skos/core#prefLabel> "some public license"@en .
									""")
			}),
			@Content(mediaType = contentTypeJSONLD, examples = {
					@ExampleObject(value = """
							[{"@id":"_:b0","@type":["http://www.w3.org/ns/locn#Address"],"http://www.w3.org/ns/locn#fullAddress":[{"@value":"Some full address here"}]},{"@id":"_:b1","@type":["http://data.europa.eu/m8g/ContactPoint"],"http://data.europa.eu/m8g/hasEmail":[{"@value":"info@data-publishing-company.com"}]},{"@id":"_:b2","@type":["http://purl.org/dc/terms/LicenseDocument"],"http://purl.org/dc/terms/type":[{"@id":"_:b3"}]},{"@id":"_:b3","@type":["http://www.w3.org/2004/02/skos/core#Concept"],"http://www.w3.org/2004/02/skos/core#prefLabel":[{"@value":"some public license","@language":"en"}]},{"@id":"http://data.europa.eu/m8g/ContactPoint"},{"@id":"http://localhost:8080/","@type":["http://www.w3.org/ns/dcat#Catalog"],"http://purl.org/dc/terms/title":[{"@value":"My LDES'es","@language":"en"}],"http://purl.org/dc/terms/description":[{"@value":"All LDES'es from publiser X","@language":"en"}],"http://purl.org/dc/terms/publisher":[{"@id":"http://sample.org/company/PublisherX"}],"http://www.w3.org/ns/dcat#dataset":[{"@id":"http://localhost:8080/parcels"}],"http://www.w3.org/ns/dcat#service":[{"@id":"http://localhost:8080/parcels/by-page/description"}]},{"@id":"http://localhost:8080/parcels","@type":["http://www.w3.org/ns/dcat#Dataset"],"http://purl.org/dc/terms/title":[{"@value":"My LDES","@language":"en"}],"http://purl.org/dc/terms/description":[{"@value":"LDES for my data collection","@language":"en"}]},{"@id":"http://localhost:8080/parcels/by-page"},{"@id":"http://localhost:8080/parcels/by-page/description","@type":["http://www.w3.org/ns/dcat#DataService"],"http://www.w3.org/ns/dcat#endpointURL":[{"@id":"http://localhost:8080/parcels/by-page"}],"http://www.w3.org/ns/dcat#servesDataset":[{"@id":"http://localhost:8080/parcels"}],"http://purl.org/dc/terms/title":[{"@value":"My geo-spatial view","@language":"en"}],"http://purl.org/dc/terms/description":[{"@value":"Geospatial fragmentation for my LDES","@language":"en"}],"http://purl.org/dc/terms/license":[{"@id":"_:b2"}]},{"@id":"http://purl.org/dc/terms/LicenseDocument"},{"@id":"http://sample.org/company/PublisherX","@type":["http://xmlns.com/foaf/0.1/Agent"],"http://xmlns.com/foaf/0.1/name":[{"@value":"Data Publishing Company"}],"http://www.w3.org/ns/legal#legalName":[{"@value":"Data Publishing Company BV"}],"http://data.europa.eu/m8g/registeredAddress":[{"@id":"_:b0"}],"http://data.europa.eu/m8g/contactPoint":[{"@id":"_:b1"}]},{"@id":"http://www.w3.org/2004/02/skos/core#Concept"},{"@id":"http://www.w3.org/ns/dcat#Catalog"},{"@id":"http://www.w3.org/ns/dcat#DataService"},{"@id":"http://www.w3.org/ns/dcat#Dataset"},{"@id":"http://www.w3.org/ns/locn#Address"},{"@id":"http://xmlns.com/foaf/0.1/Agent"}]
							""")
			})
	})
	@ApiResponse(responseCode = "500", description = "The validation report of the invalid dcat.", content = {
			@Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = Model.class), examples = {
					@ExampleObject(value = """
							@prefix company: <http://sample.org/company/> .
							@prefix dc:      <http://purl.org/dc/elements/1.1/> .
							@prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
							@prefix sh:      <http://www.w3.org/ns/shacl#> .
							@prefix terms:   <http://purl.org/dc/terms/> .

							[ rdf:type     sh:ValidationReport ;
							  sh:conforms  false ;
							  sh:result    [ rdf:type                      sh:ValidationResult ;
							                 sh:focusNode                  <http://localhost:8080> ;
							                 sh:resultMessage              "ClassConstraint[<http://xmlns.com/foaf/0.1/Agent>]: Expected class :<http://xmlns.com/foaf/0.1/Agent> for <http://sample.org/company/PublisherX>" ;
							                 sh:resultPath                 terms:publisher ;
							                 sh:resultSeverity             sh:Violation ;
							                 sh:sourceConstraintComponent  sh:ClassConstraintComponent ;
							                 sh:sourceShape                []  ;
							                 sh:value                      company:PublisherX
							               ]
							] .
									""")
			}),
			@Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = {
					@ExampleObject(value = """
							_:genid1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/shacl#ValidationReport> .
							_:genid1 <http://www.w3.org/ns/shacl#conforms> "false"^^<http://www.w3.org/2001/XMLSchema#boolean> .
							_:genid1 <http://www.w3.org/ns/shacl#result> _:genid2 .
							_:genid2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/shacl#ValidationResult> .
							_:genid2 <http://www.w3.org/ns/shacl#focusNode> <http://localhost:8080> .
							_:genid2 <http://www.w3.org/ns/shacl#resultMessage> "ClassConstraint[<http://xmlns.com/foaf/0.1/Agent>]: Expected class :<http://xmlns.com/foaf/0.1/Agent> for <http://sample.org/company/PublisherX>" .
							_:genid2 <http://www.w3.org/ns/shacl#resultPath> <http://purl.org/dc/terms/publisher> .
							_:genid2 <http://www.w3.org/ns/shacl#resultSeverity> <http://www.w3.org/ns/shacl#Violation> .
							_:genid2 <http://www.w3.org/ns/shacl#sourceConstraintComponent> <http://www.w3.org/ns/shacl#ClassConstraintComponent> .
							_:genid2 <http://www.w3.org/ns/shacl#sourceShape> _:genid3 .
							_:genid2 <http://www.w3.org/ns/shacl#value> <http://sample.org/company/PublisherX> .
										""")
			}),
			@Content(mediaType = contentTypeJSONLD, examples = {
					@ExampleObject(value = """
												[{"@type":["http://www.w3.org/ns/shacl#ValidationReport"],"http://www.w3.org/ns/shacl#conforms":[{"@value":false}],"http://www.w3.org/ns/shacl#result":[{"@id":"_:b1"}]},{"@id":"_:b1","@type":["http://www.w3.org/ns/shacl#ValidationResult"],"http://www.w3.org/ns/shacl#focusNode":[{"@id":"http://localhost:8080"}],"http://www.w3.org/ns/shacl#resultMessage":[{"@value":"ClassConstraint[<http://xmlns.com/foaf/0.1/Agent>]: Expected class :<http://xmlns.com/foaf/0.1/Agent> for <http://sample.org/company/PublisherX>"}],"http://www.w3.org/ns/shacl#resultPath":[{"@id":"http://purl.org/dc/terms/publisher"}],"http://www.w3.org/ns/shacl#resultSeverity":[{"@id":"http://www.w3.org/ns/shacl#Violation"}],"http://www.w3.org/ns/shacl#sourceConstraintComponent":[{"@id":"http://www.w3.org/ns/shacl#ClassConstraintComponent"}],"http://www.w3.org/ns/shacl#sourceShape":[{"@id":"_:b2"}],"http://www.w3.org/ns/shacl#value":[{"@id":"http://sample.org/company/PublisherX"}]},{"@id":"_:b2"},{"@id":"http://localhost:8080"},{"@id":"http://purl.org/dc/terms/publisher"},{"@id":"http://sample.org/company/PublisherX"},{"@id":"http://www.w3.org/ns/shacl#ClassConstraintComponent"},{"@id":"http://www.w3.org/ns/shacl#ValidationReport"},{"@id":"http://www.w3.org/ns/shacl#ValidationResult"},{"@id":"http://www.w3.org/ns/shacl#Violation"}]
							""")
			})
	})
	@Operation(summary = "The combined metadata for the server is retrieved")
	@GetMapping
	ResponseEntity<Model> getDcat(@Parameter(hidden = true) String language, HttpServletResponse response);

	@ApiResponse(responseCode = "201", description = "The generated UUID that is linked to the created DCAT", content = @Content(mediaType = contentTypeTextPlain, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = "e1c9443a-ab9f-407f-a65b-09d69f481966")))
	@ApiResponse(responseCode = "400", content = @Content, description = "Provided DCAT configuration is not valid")
	@Operation(summary = "Add DCAT configuration for the server")
	String postServerDcat(
			@RequestBody(description = "A blank node of type dcat:Catalog with only dcat:Catalog properties " +
					"(e.g. dct:title) and relations (e.g. dct:publisher), excluding relations " +
					"(non-configurable metadata) to dcat:Dataset and dcat:DataService", content = {
							@Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = Model.class), examples = {
									@ExampleObject(value = """
											@prefix dct:   <http://purl.org/dc/terms/> .
											@prefix dcat:  <http://www.w3.org/ns/dcat#> .

											[] a dcat:Catalog ;
											  dct:title "My LDES'es"@en ;
											  dct:description "All LDES'es from publiser X"@en .
											""")
							}),
							@Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = {
									@ExampleObject(value = """
											_:genid1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/dcat#Catalog> .
											_:genid1 <http://purl.org/dc/terms/title> "My LDES'es"@en .
											_:genid1 <http://purl.org/dc/terms/description> "All LDES'es from publiser X"@en .
											""")
							}),
							@Content(mediaType = contentTypeJSONLD, schema = @Schema(implementation = String.class), examples = {
									@ExampleObject(value = """
											[{"@type":["http://www.w3.org/ns/dcat#Catalog"],
											"http://purl.org/dc/terms/title":[{"@value":"My LDES'es","@language":"en"}],
											"http://purl.org/dc/terms/description":[{"@value":"All LDES'es from publiser X","@language":"en"}]},{"@id":"http://www.w3.org/ns/dcat#Catalog"}]
											""")
							})
					}) Model dcat);

	@ApiResponse(responseCode = "200")
	@ApiResponse(responseCode = "400", description = "Provided DCAT configuration is not valid")
	@ApiResponse(responseCode = "404", description = "No DCAT configuration found with provided id")
	@Operation(summary = "Update DCAT configuration for the server")
	void putServerDcat(
			@Parameter(name = "catalogId", content = @Content(schema = @Schema(implementation = String.class), examples = @ExampleObject(value = "e1c9443a-ab9f-407f-a65b-09d69f481966"))) String catalogId,
			@RequestBody(description = "A blank node of type dcat:Catalog with only dcat:Catalog properties " +
					"(e.g. dct:title) and relations (e.g. dct:publisher), excluding relations " +
					"(non-configurable metadata) to dcat:Dataset and dcat:DataService", content = {
							@Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = Model.class), examples = {
									@ExampleObject(value = """
											@prefix dct:   <http://purl.org/dc/terms/> .
											@prefix dcat:  <http://www.w3.org/ns/dcat#> .

											[] a dcat:Catalog ;
											  dct:title "My LDES'es"@en ;
											  dct:description "All LDES'es from publiser X"@en .
											""")
							}),
							@Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = {
									@ExampleObject(value = """
											_:genid1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/dcat#Catalog> .
											_:genid1 <http://purl.org/dc/terms/title> "My LDES'es"@en .
											_:genid1 <http://purl.org/dc/terms/description> "All LDES'es from publiser X"@en .
											""")
							}),
							@Content(mediaType = contentTypeJSONLD, schema = @Schema(implementation = String.class), examples = {
									@ExampleObject(value = """
											[{"@type":["http://www.w3.org/ns/dcat#Catalog"],"http://purl.org/dc/terms/title":[{"@value":"My LDES'es","@language":"en"}],"http://purl.org/dc/terms/description":[{"@value":"All LDES'es from publiser X","@language":"en"}]},{"@id":"http://www.w3.org/ns/dcat#Catalog"}]
											""")
							})
					}) Model dcat);

	@ApiResponse(responseCode = "200")
	@Operation(summary = "Delete DCAT configuration for the server")
	void deleteServerDcat(
			@Parameter(name = "catalogId", content = @Content(schema = @Schema(implementation = String.class), examples = @ExampleObject(value = "e1c9443a-ab9f-407f-a65b-09d69f481966"))) String catalogId);
}
