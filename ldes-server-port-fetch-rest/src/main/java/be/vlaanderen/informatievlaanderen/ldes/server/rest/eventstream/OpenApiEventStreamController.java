package be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.jena.rdf.model.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import static org.apache.jena.riot.WebContent.*;

@Tag(name = "Event Streams")
@SuppressWarnings("java:S2479") // whitespace needed for examples
public interface OpenApiEventStreamController {

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
	@Operation(summary = "The combined metadata for the server is retrieved")
	@GetMapping
	Model getDcat(@Parameter(hidden = true) String language, HttpServletResponse response);

	@Operation(summary = "Retrieve an Linked Data Event Stream")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
					@prefix ldes: <https://w3id.org/ldes#> .
					@prefix custom: <http://example.org/> .
					@prefix dcterms: <http://purl.org/dc/terms/> .
					@prefix tree: <https://w3id.org/tree#>.
					@prefix sh:   <http://www.w3.org/ns/shacl#> .
					@prefix server: <http://localhost:8080/> .
					@prefix xsd:  <http://www.w3.org/2001/XMLSchema#> .
					@prefix example: <http://example.org/> .
					@prefix parcels: <http://localhost:8080/parcels/> .

					server:parcels a ldes:EventStream ;
						ldes:timestampPath dcterms:created ;
						ldes:versionOfPath dcterms:isVersionOf ;
						custom:memberType <https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel> ;
						custom:hasDefaultView "true"^^xsd:boolean ;
						tree:shape parcels:shape .
						ldes:view parcels:pagination .

					parcels:pagination tree:viewDescription [
						example:fragmentationStrategy [
							a example:Fragmentation ;
							example:name "pagination" ;
							example:memberLimit "100"
						] ;
					] .

					parcels:shape a sh:NodeShape ;
						sh:nodeShape [
							sh:closed true ;
							sh:propertyShape []
							] ;
						sh:deactivated true .
					""")),
			@Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
					<http://localhost:8080/parcels> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/ldes#EventStream> .
					<http://localhost:8080/parcels> <https://w3id.org/ldes#timestampPath> <http://purl.org/dc/terms/created> .
					<http://localhost:8080/parcels> <https://w3id.org/ldes#versionOfPath> <http://purl.org/dc/terms/isVersionOf> .
					<http://localhost:8080/parcels> <http://example.org/memberType> <https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel> .
					<http://localhost:8080/parcels> <http://example.org/hasDefaultView> "true"^^<http://www.w3.org/2001/XMLSchema#boolean> .
					<http://localhost:8080/parcels> <https://w3id.org/tree#shape> <http://localhost:8080/parcels/shape> .
					<https://w3id.org/ldes#view> <http://localhost:8080/parcels/pagination> ""^^<http://www.w3.org/2001/XMLSchema#integer> .
					_:genid2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://example.org/Fragmentation> .
					_:genid2 <http://example.org/name> "pagination" .
					_:genid2 <http://example.org/memberLimit> "100" .
					_:genid1 <http://example.org/fragmentationStrategy> _:genid2 .
					<http://localhost:8080/parcels/pagination> <https://w3id.org/tree#viewDescription> _:genid1 .
					<http://localhost:8080/parcels/shape> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/shacl#NodeShape> .
					<http://localhost:8080/parcels/shape> <http://www.w3.org/ns/shacl#nodeShape> _:genid3 .
					<http://localhost:8080/parcels/shape> <http://www.w3.org/ns/shacl#deactivated> "true"^^<http://www.w3.org/2001/XMLSchema#boolean> .
					_:genid3 <http://www.w3.org/ns/shacl#closed> "true"^^<http://www.w3.org/2001/XMLSchema#boolean> .
					_:genid3 <http://www.w3.org/ns/shacl#propertyShape> _:genid4 .
					""")),
			@Content(mediaType = contentTypeJSONLD, examples = @ExampleObject(value = """
					[{"@id":"_:b0","@type":["http://example.org/Fragmentation"],"http://example.org/name":[{"@value":"pagination"}],"http://example.org/memberLimit":[{"@value":"100"}]},{"@id":"_:b1","http://example.org/fragmentationStrategy":[{"@id":"_:b0"}]},{"@id":"_:b2","http://www.w3.org/ns/shacl#closed":[{"@value":true}],"http://www.w3.org/ns/shacl#propertyShape":[{"@id":"_:b3"}]},{"@id":"_:b3"},{"@id":"http://example.org/Fragmentation"},{"@id":"http://localhost:8080/parcels","@type":["https://w3id.org/ldes#EventStream"],"https://w3id.org/ldes#timestampPath":[{"@id":"http://purl.org/dc/terms/created"}],"https://w3id.org/ldes#versionOfPath":[{"@id":"http://purl.org/dc/terms/isVersionOf"}],"http://example.org/memberType":[{"@id":"https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel"}],"http://example.org/hasDefaultView":[{"@value":true}],"https://w3id.org/tree#shape":[{"@id":"http://localhost:8080/parcels/shape"}]},{"@id":"http://localhost:8080/parcels/pagination","https://w3id.org/tree#viewDescription":[{"@id":"_:b1"}]},{"@id":"http://localhost:8080/parcels/shape","@type":["http://www.w3.org/ns/shacl#NodeShape"],"http://www.w3.org/ns/shacl#nodeShape":[{"@id":"_:b2"}],"http://www.w3.org/ns/shacl#deactivated":[{"@value":true}]},{"@id":"http://purl.org/dc/terms/created"},{"@id":"http://purl.org/dc/terms/isVersionOf"},{"@id":"http://www.w3.org/ns/shacl#NodeShape"},{"@id":"https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel"},{"@id":"https://w3id.org/ldes#EventStream"},{"@id":"https://w3id.org/ldes#view","http://localhost:8080/parcels/pagination":[{"@value":"","@type":"http://www.w3.org/2001/XMLSchema#integer"}]}]
										"""))
	})
	@ApiResponse(responseCode = "404", description = "Event Stream with provided collection name could not be found")
	ResponseEntity<EventStreamResponse> retrieveLdesFragment(@Parameter(hidden = true) String language,
			HttpServletResponse response,
			@Parameter(example = "parcels") String collectionName);
}
