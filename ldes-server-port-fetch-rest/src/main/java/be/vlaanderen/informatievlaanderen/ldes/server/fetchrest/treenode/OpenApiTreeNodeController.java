package be.vlaanderen.informatievlaanderen.ldes.server.fetchrest.treenode;

import be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.entities.TreeNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.apache.jena.riot.WebContent.*;

@Tag(name = "Fetch")
@SuppressWarnings("java:S2479")
public interface OpenApiTreeNodeController {

	@Operation(summary = "Retrieve an LDES Fragment")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
					@prefix ns0: <https://w3id.org/tree#> .
					@prefix ns1: <https://w3id.org/ldes#> .
					@prefix prov: <http://www.w3.org/ns/prov#> .
					@prefix dc: <http://purl.org/dc/terms/> .
					@prefix skos: <http://www.w3.org/2004/02/skos/core#> .
					@prefix ns2: <http://www.w3.org/ns/locn#> .
					@prefix ns3: <http://www.opengis.net/ont/geosparql#> .
					@prefix ns4: <https://data.vlaanderen.be/ns/mobiliteit#Zone.> .
					@prefix ns5: <https://data.vlaanderen.be/ns/mobiliteit#> .
					@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
					@prefix dc11: <http://purl.org/dc/elements/1.1/> .
					@prefix ns6: <http://data.europa.eu/m8g/> .
					@prefix ns7: <http://www.w3.org/ns/adms#> .
					@prefix ns8: <https://gipod.vlaanderen.be/ns/gipod#> .
					@prefix schema: <http://schema.org/> .
					@prefix ns9: <https://data.vlaanderen.be/ns/generiek#Tijdsschema.> .
					@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
					@prefix org: <http://www.w3.org/ns/org#> .

					<http://localhost:8089/exampleData>
					  ns0:member <https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/29797> ;
					  a <https://w3id.org/ldes#EventStream> ;
					  ns1:timestampPath prov:generatedAtTime ;
					  ns1:versionOf dc:isVersionOf ;
					  ns0:shape <http://localhost:8089/exampleData/shape> ;
					  ns0:view <http://localhost:8089/exampleData?fragment=1> .

					<https://private-api.gipod.test-vlaanderen.be/api/v1/taxonomies/statuses/0a4ee99b-8b8a-47c8-913f-117220febee0> skos:prefLabel "In opmaak"@nl-BE .
					<https://private-api.gipod.test-vlaanderen.be/api/v1/taxonomies/mobility-hindrance/consequencetypes/ee31fd67-b75e-4499-9ad4-0a595717a9c7> skos:prefLabel "Afgesloten in 1 rijrichting"@nl-BE .
					<https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/zones/72facaaa-f26b-4ec0-ac2d-cff24961f13d>
					  a <https://data.vlaanderen.be/ns/mobiliteit#Zone> ;
					  ns2:geometry [
					    a ns2:Geometry ;
					    ns3:asWKT "POLYGON ((122980.5 183762, 122973.5 183742.75, 123007 183740.5, 122980.5 183762))"^^ns3:wktLiteral
					  ] ;
					  ns4:type <https://private-api.gipod.test-vlaanderen.be/api/v1/taxonomies/zonetypes/0fb72ef7-6ac9-4a70-b295-a30ea215d250> ;
					  ns5:gevolg <https://private-api.gipod.test-vlaanderen.be/api/v1/taxonomies/mobility-hindrance/consequencetypes/ee31fd67-b75e-4499-9ad4-0a595717a9c7> .

					<https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/29797>
					  dc:created "2021-02-17T14:16:59.2053421Z"^^xsd:dateTime ;
					  dc11:creator <https://private-api.gipod.test-vlaanderen.be/api/v1/organisations/9c5926df-195d-01be-a40a-360bcd21d662> ;
					  ns5:periode [
					    a <http://data.europa.eu/m8g/PeriodOfTime> ;
					    ns6:endTime "2022-05-14T11:09:00Z"^^xsd:dateTime ;
					    ns6:startTime "2022-05-12T11:09:00Z"^^xsd:dateTime
					  ], [
					    a ns6:PeriodOfTime ;
					    ns6:endTime "2022-05-22T11:09:00Z"^^xsd:dateTime ;
					    ns6:startTime "2022-05-20T11:09:00Z"^^xsd:dateTime
					  ] ;
					  dc:modified "2021-02-17T14:16:59.2143997Z"^^xsd:dateTime ;
					  prov:generatedAtTime "2021-02-17T14:16:59.233Z"^^xsd:dateTime ;
					  ns5:zone <https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/zones/72facaaa-f26b-4ec0-ac2d-cff24961f13d> ;
					  ns5:Inname.status <https://private-api.gipod.test-vlaanderen.be/api/v1/taxonomies/statuses/0a4ee99b-8b8a-47c8-913f-117220febee0> ;
					  dc:isVersionOf <https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919> ;
					  ns7:versionNotes "MobilityHindranceZoneWasAdded"@nl-BE ;
					  ns7:identifier [
					    a ns7:Identifier ;
					    skos:notation "10034919"^^ns8:gipodId ;
					    ns7:schemaAgency "https://gipod.vlaanderen.be"@nl-BE
					  ] ;
					  ns5:beheerder <https://private-api.gipod.test-vlaanderen.be/api/v1/organisations/9c5926df-195d-01be-a40a-360bcd21d662> ;
					  schema:eventSchedule [
					    schema:repeatFrequency "P1M"@nl-BE ;
					    a <https://schema.org/Schedule> ;
					    schema:endTime "11:09Z"^^xsd:time ;
					    schema:byMonth 10, 1, 3, 6 ;
					    schema:endDate "2022-10-17"^^xsd:date ;
					    schema:repeatCount 12 ;
					    schema:exceptDate "2023-02-15"^^xsd:date ;
					    schema:startDate "2022-10-15"^^xsd:date ;
					    schema:startTime "11:09Z"^^xsd:time ;
					    ns9:duur "P2D"^^xsd:duration
					  ] ;
					  dc11:contributor <https://private-api.gipod.test-vlaanderen.be/api/v1/organisations/9c5926df-195d-01be-a40a-360bcd21d662> ;
					  dc:description "Description of the mobility hindrance"^^rdf:langString ;
					  ns8:gipodId 10034919 ;
					  a ns5:Mobiliteitshinder .

					<https://private-api.gipod.test-vlaanderen.be/api/v1/taxonomies/zonetypes/0fb72ef7-6ac9-4a70-b295-a30ea215d250> skos:prefLabel "HinderZone"@nl-BE .
					<http://localhost:8089/exampleData?fragment=1> a ns0:Node .
					<https://private-api.gipod.test-vlaanderen.be/api/v1/organisations/9c5926df-195d-01be-a40a-360bcd21d662>
					  a org:Organization ;
					  skos:prefLabel "AIV"^^rdf:langString .
					""")),
			@Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
					<http://localhost:8089/exampleData> <https://w3id.org/tree#member> <https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/29797> .
					<http://localhost:8089/exampleData> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/ldes#EventStream> .
					<http://localhost:8089/exampleData> <https://w3id.org/ldes#timestampPath> <http://www.w3.org/ns/prov#generatedAtTime> .
					<http://localhost:8089/exampleData> <https://w3id.org/ldes#versionOf> <http://purl.org/dc/terms/isVersionOf> .
					<http://localhost:8089/exampleData> <https://w3id.org/tree#shape> <http://localhost:8089/exampleData/shape> .
					<http://localhost:8089/exampleData> <https://w3id.org/tree#view> <http://localhost:8089/exampleData?fragment=1> .
					_:B8f75db9b34a20dd2830fbf6ffd1eb5fc <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://data.europa.eu/m8g/PeriodOfTime> .
					_:B8f75db9b34a20dd2830fbf6ffd1eb5fc <http://data.europa.eu/m8g/endTime> "2022-05-14T11:09:00Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
					_:B8f75db9b34a20dd2830fbf6ffd1eb5fc <http://data.europa.eu/m8g/startTime> "2022-05-12T11:09:00Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
					_:Be1e8449b9b364044dd067b5a1f995c53 <http://schema.org/repeatFrequency> "P1M"@nl-BE .
					_:Be1e8449b9b364044dd067b5a1f995c53 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://schema.org/Schedule> .
					_:Be1e8449b9b364044dd067b5a1f995c53 <http://schema.org/endTime> "11:09Z"^^<http://www.w3.org/2001/XMLSchema#time> .
					_:Be1e8449b9b364044dd067b5a1f995c53 <http://schema.org/byMonth> "10"^^<http://www.w3.org/2001/XMLSchema#integer> .
					_:Be1e8449b9b364044dd067b5a1f995c53 <http://schema.org/byMonth> "1"^^<http://www.w3.org/2001/XMLSchema#integer> .
					_:Be1e8449b9b364044dd067b5a1f995c53 <http://schema.org/endDate> "2022-10-17"^^<http://www.w3.org/2001/XMLSchema#date> .
					_:Be1e8449b9b364044dd067b5a1f995c53 <http://schema.org/byMonth> "3"^^<http://www.w3.org/2001/XMLSchema#integer> .
					_:Be1e8449b9b364044dd067b5a1f995c53 <http://schema.org/repeatCount> "12"^^<http://www.w3.org/2001/XMLSchema#integer> .
					_:Be1e8449b9b364044dd067b5a1f995c53 <http://schema.org/exceptDate> "2023-02-15"^^<http://www.w3.org/2001/XMLSchema#date> .
					_:Be1e8449b9b364044dd067b5a1f995c53 <http://schema.org/byMonth> "6"^^<http://www.w3.org/2001/XMLSchema#integer> .
					_:Be1e8449b9b364044dd067b5a1f995c53 <http://schema.org/startDate> "2022-10-15"^^<http://www.w3.org/2001/XMLSchema#date> .
					_:Be1e8449b9b364044dd067b5a1f995c53 <http://schema.org/startTime> "11:09Z"^^<http://www.w3.org/2001/XMLSchema#time> .
					_:Be1e8449b9b364044dd067b5a1f995c53 <https://data.vlaanderen.be/ns/generiek#Tijdsschema.duur> "P2D"^^<http://www.w3.org/2001/XMLSchema#duration> .
					<https://private-api.gipod.test-vlaanderen.be/api/v1/taxonomies/statuses/0a4ee99b-8b8a-47c8-913f-117220febee0> <http://www.w3.org/2004/02/skos/core#prefLabel> "In opmaak"@nl-BE .
					<https://private-api.gipod.test-vlaanderen.be/api/v1/taxonomies/mobility-hindrance/consequencetypes/ee31fd67-b75e-4499-9ad4-0a595717a9c7> <http://www.w3.org/2004/02/skos/core#prefLabel> "Afgesloten in 1 rijrichting"@nl-BE .
					_:B5bdcb08fcb9992194bb0b893252772bb <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/locn#Geometry> .
					_:B5bdcb08fcb9992194bb0b893252772bb <http://www.opengis.net/ont/geosparql#asWKT> "POLYGON ((122980.5 183762, 122973.5 183742.75, 123007 183740.5, 122980.5 183762))"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .
					<https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/zones/72facaaa-f26b-4ec0-ac2d-cff24961f13d> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://data.vlaanderen.be/ns/mobiliteit#Zone> .
					<https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/zones/72facaaa-f26b-4ec0-ac2d-cff24961f13d> <http://www.w3.org/ns/locn#geometry> _:B5bdcb08fcb9992194bb0b893252772bb .
					<https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/zones/72facaaa-f26b-4ec0-ac2d-cff24961f13d> <https://data.vlaanderen.be/ns/mobiliteit#Zone.type> <https://private-api.gipod.test-vlaanderen.be/api/v1/taxonomies/zonetypes/0fb72ef7-6ac9-4a70-b295-a30ea215d250> .
					<https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/zones/72facaaa-f26b-4ec0-ac2d-cff24961f13d> <https://data.vlaanderen.be/ns/mobiliteit#gevolg> <https://private-api.gipod.test-vlaanderen.be/api/v1/taxonomies/mobility-hindrance/consequencetypes/ee31fd67-b75e-4499-9ad4-0a595717a9c7> .
					<https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/29797> <http://purl.org/dc/terms/created> "2021-02-17T14:16:59.2053421Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
					<https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/29797> <http://purl.org/dc/elements/1.1/creator> <https://private-api.gipod.test-vlaanderen.be/api/v1/organisations/9c5926df-195d-01be-a40a-360bcd21d662> .
					<https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/29797> <https://data.vlaanderen.be/ns/mobiliteit#periode> _:B8f75db9b34a20dd2830fbf6ffd1eb5fc .
					<https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/29797> <http://purl.org/dc/terms/modified> "2021-02-17T14:16:59.2143997Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
					<https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/29797> <http://www.w3.org/ns/prov#generatedAtTime> "2021-02-17T14:16:59.233Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
					<https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/29797> <https://data.vlaanderen.be/ns/mobiliteit#zone> <https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/zones/72facaaa-f26b-4ec0-ac2d-cff24961f13d> .
					<https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/29797> <https://data.vlaanderen.be/ns/mobiliteit#Inname.status> <https://private-api.gipod.test-vlaanderen.be/api/v1/taxonomies/statuses/0a4ee99b-8b8a-47c8-913f-117220febee0> .
					<https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/29797> <http://purl.org/dc/terms/isVersionOf> <https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919> .
					<https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/29797> <http://www.w3.org/ns/adms#versionNotes> "MobilityHindranceZoneWasAdded"@nl-BE .
					<https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/29797> <https://data.vlaanderen.be/ns/mobiliteit#periode> _:B597e55c9b93e03b7d96813389e1dea14 .
					<https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/29797> <http://www.w3.org/ns/adms#identifier> _:Bc11eba98791db6785f1184c57734922e .
					<https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/29797> <https://data.vlaanderen.be/ns/mobiliteit#beheerder> <https://private-api.gipod.test-vlaanderen.be/api/v1/organisations/9c5926df-195d-01be-a40a-360bcd21d662> .
					<https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/29797> <http://schema.org/eventSchedule> _:Be1e8449b9b364044dd067b5a1f995c53 .
					<https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/29797> <http://purl.org/dc/elements/1.1/contributor> <https://private-api.gipod.test-vlaanderen.be/api/v1/organisations/9c5926df-195d-01be-a40a-360bcd21d662> .
					<https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/29797> <http://purl.org/dc/terms/description> "Description of the mobility hindrance"^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#langString> .
					<https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/29797> <https://gipod.vlaanderen.be/ns/gipod#gipodId> "10034919"^^<http://www.w3.org/2001/XMLSchema#integer> .
					<https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/29797> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder> .
					_:Bc11eba98791db6785f1184c57734922e <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/adms#Identifier> .
					_:Bc11eba98791db6785f1184c57734922e <http://www.w3.org/2004/02/skos/core#notation> "10034919"^^<https://gipod.vlaanderen.be/ns/gipod#gipodId> .
					_:Bc11eba98791db6785f1184c57734922e <http://www.w3.org/ns/adms#schemaAgency> "https://gipod.vlaanderen.be"@nl-BE .
					<https://private-api.gipod.test-vlaanderen.be/api/v1/taxonomies/zonetypes/0fb72ef7-6ac9-4a70-b295-a30ea215d250> <http://www.w3.org/2004/02/skos/core#prefLabel> "HinderZone"@nl-BE .
					<http://localhost:8089/exampleData?fragment=1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/tree#Node> .
					_:B597e55c9b93e03b7d96813389e1dea14 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://data.europa.eu/m8g/PeriodOfTime> .
					_:B597e55c9b93e03b7d96813389e1dea14 <http://data.europa.eu/m8g/endTime> "2022-05-22T11:09:00Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
					_:B597e55c9b93e03b7d96813389e1dea14 <http://data.europa.eu/m8g/startTime> "2022-05-20T11:09:00Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
					<https://private-api.gipod.test-vlaanderen.be/api/v1/organisations/9c5926df-195d-01be-a40a-360bcd21d662> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/org#Organization> .
					<https://private-api.gipod.test-vlaanderen.be/api/v1/organisations/9c5926df-195d-01be-a40a-360bcd21d662> <http://www.w3.org/2004/02/skos/core#prefLabel> "AIV"^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#langString> .
					""")),
			@Content(mediaType = contentTypeJSONLD, examples = @ExampleObject(value = """
					[{"@id":"_:b0","@type":["http://data.europa.eu/m8g/PeriodOfTime"],"http://data.europa.eu/m8g/endTime":[{"@value":"2022-05-14T11:09:00Z","@type":"http://www.w3.org/2001/XMLSchema#dateTime"}],"http://data.europa.eu/m8g/startTime":[{"@value":"2022-05-12T11:09:00Z","@type":"http://www.w3.org/2001/XMLSchema#dateTime"}]},{"@id":"_:b1","http://schema.org/repeatFrequency":[{"@value":"P1M","@language":"nl-BE"}],"@type":["https://schema.org/Schedule"],"http://schema.org/endTime":[{"@value":"11:09Z","@type":"http://www.w3.org/2001/XMLSchema#time"}],"http://schema.org/byMonth":[{"@value":10},{"@value":1},{"@value":3},{"@value":6}],"http://schema.org/endDate":[{"@value":"2022-10-17","@type":"http://www.w3.org/2001/XMLSchema#date"}],"http://schema.org/repeatCount":[{"@value":12}],"http://schema.org/exceptDate":[{"@value":"2023-02-15","@type":"http://www.w3.org/2001/XMLSchema#date"}],"http://schema.org/startDate":[{"@value":"2022-10-15","@type":"http://www.w3.org/2001/XMLSchema#date"}],"http://schema.org/startTime":[{"@value":"11:09Z","@type":"http://www.w3.org/2001/XMLSchema#time"}],"https://data.vlaanderen.be/ns/generiek#Tijdsschema.duur":[{"@value":"P2D","@type":"http://www.w3.org/2001/XMLSchema#duration"}]},{"@id":"_:b2","@type":["http://www.w3.org/ns/locn#Geometry"],"http://www.opengis.net/ont/geosparql#asWKT":[{"@value":"POLYGON ((122980.5 183762, 122973.5 183742.75, 123007 183740.5, 122980.5 183762))","@type":"http://www.opengis.net/ont/geosparql#wktLiteral"}]},{"@id":"_:b3","@type":["http://data.europa.eu/m8g/PeriodOfTime"],"http://data.europa.eu/m8g/endTime":[{"@value":"2022-05-22T11:09:00Z","@type":"http://www.w3.org/2001/XMLSchema#dateTime"}],"http://data.europa.eu/m8g/startTime":[{"@value":"2022-05-20T11:09:00Z","@type":"http://www.w3.org/2001/XMLSchema#dateTime"}]},{"@id":"_:b4","@type":["http://www.w3.org/ns/adms#Identifier"],"http://www.w3.org/2004/02/skos/core#notation":[{"@value":"10034919","@type":"https://gipod.vlaanderen.be/ns/gipod#gipodId"}],"http://www.w3.org/ns/adms#schemaAgency":[{"@value":"https://gipod.vlaanderen.be","@language":"nl-BE"}]},{"@id":"http://data.europa.eu/m8g/PeriodOfTime"},{"@id":"http://localhost:8089/exampleData","https://w3id.org/tree#member":[{"@id":"https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/29797"}],"@type":["https://w3id.org/ldes#EventStream"],"https://w3id.org/ldes#timestampPath":[{"@id":"http://www.w3.org/ns/prov#generatedAtTime"}],"https://w3id.org/ldes#versionOf":[{"@id":"http://purl.org/dc/terms/isVersionOf"}],"https://w3id.org/tree#shape":[{"@id":"http://localhost:8089/exampleData/shape"}],"https://w3id.org/tree#view":[{"@id":"http://localhost:8089/exampleData?fragment=1"}]},{"@id":"http://localhost:8089/exampleData/shape"},{"@id":"http://localhost:8089/exampleData?fragment=1","@type":["https://w3id.org/tree#Node"]},{"@id":"http://purl.org/dc/terms/isVersionOf"},{"@id":"http://www.w3.org/ns/adms#Identifier"},{"@id":"http://www.w3.org/ns/locn#Geometry"},{"@id":"http://www.w3.org/ns/org#Organization"},{"@id":"http://www.w3.org/ns/prov#generatedAtTime"},{"@id":"https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder"},{"@id":"https://data.vlaanderen.be/ns/mobiliteit#Zone"},{"@id":"https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919"},{"@id":"https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/29797","http://purl.org/dc/terms/created":[{"@value":"2021-02-17T14:16:59.2053421Z","@type":"http://www.w3.org/2001/XMLSchema#dateTime"}],"http://purl.org/dc/elements/1.1/creator":[{"@id":"https://private-api.gipod.test-vlaanderen.be/api/v1/organisations/9c5926df-195d-01be-a40a-360bcd21d662"}],"https://data.vlaanderen.be/ns/mobiliteit#periode":[{"@id":"_:b0"},{"@id":"_:b3"}],"http://purl.org/dc/terms/modified":[{"@value":"2021-02-17T14:16:59.2143997Z","@type":"http://www.w3.org/2001/XMLSchema#dateTime"}],"http://www.w3.org/ns/prov#generatedAtTime":[{"@value":"2021-02-17T14:16:59.233Z","@type":"http://www.w3.org/2001/XMLSchema#dateTime"}],"https://data.vlaanderen.be/ns/mobiliteit#zone":[{"@id":"https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/zones/72facaaa-f26b-4ec0-ac2d-cff24961f13d"}],"https://data.vlaanderen.be/ns/mobiliteit#Inname.status":[{"@id":"https://private-api.gipod.test-vlaanderen.be/api/v1/taxonomies/statuses/0a4ee99b-8b8a-47c8-913f-117220febee0"}],"http://purl.org/dc/terms/isVersionOf":[{"@id":"https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919"}],"http://www.w3.org/ns/adms#versionNotes":[{"@value":"MobilityHindranceZoneWasAdded","@language":"nl-BE"}],"http://www.w3.org/ns/adms#identifier":[{"@id":"_:b4"}],"https://data.vlaanderen.be/ns/mobiliteit#beheerder":[{"@id":"https://private-api.gipod.test-vlaanderen.be/api/v1/organisations/9c5926df-195d-01be-a40a-360bcd21d662"}],"http://schema.org/eventSchedule":[{"@id":"_:b1"}],"http://purl.org/dc/elements/1.1/contributor":[{"@id":"https://private-api.gipod.test-vlaanderen.be/api/v1/organisations/9c5926df-195d-01be-a40a-360bcd21d662"}],"http://purl.org/dc/terms/description":[{"@value":"Description of the mobility hindrance","@type":"http://www.w3.org/1999/02/22-rdf-syntax-ns#langString"}],"https://gipod.vlaanderen.be/ns/gipod#gipodId":[{"@value":10034919}],"@type":["https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder"]},{"@id":"https://private-api.gipod.test-vlaanderen.be/api/v1/mobility-hindrances/10034919/zones/72facaaa-f26b-4ec0-ac2d-cff24961f13d","@type":["https://data.vlaanderen.be/ns/mobiliteit#Zone"],"http://www.w3.org/ns/locn#geometry":[{"@id":"_:b2"}],"https://data.vlaanderen.be/ns/mobiliteit#Zone.type":[{"@id":"https://private-api.gipod.test-vlaanderen.be/api/v1/taxonomies/zonetypes/0fb72ef7-6ac9-4a70-b295-a30ea215d250"}],"https://data.vlaanderen.be/ns/mobiliteit#gevolg":[{"@id":"https://private-api.gipod.test-vlaanderen.be/api/v1/taxonomies/mobility-hindrance/consequencetypes/ee31fd67-b75e-4499-9ad4-0a595717a9c7"}]},{"@id":"https://private-api.gipod.test-vlaanderen.be/api/v1/organisations/9c5926df-195d-01be-a40a-360bcd21d662","@type":["http://www.w3.org/ns/org#Organization"],"http://www.w3.org/2004/02/skos/core#prefLabel":[{"@value":"AIV","@type":"http://www.w3.org/1999/02/22-rdf-syntax-ns#langString"}]},{"@id":"https://private-api.gipod.test-vlaanderen.be/api/v1/taxonomies/mobility-hindrance/consequencetypes/ee31fd67-b75e-4499-9ad4-0a595717a9c7","http://www.w3.org/2004/02/skos/core#prefLabel":[{"@value":"Afgesloten in 1 rijrichting","@language":"nl-BE"}]},{"@id":"https://private-api.gipod.test-vlaanderen.be/api/v1/taxonomies/statuses/0a4ee99b-8b8a-47c8-913f-117220febee0","http://www.w3.org/2004/02/skos/core#prefLabel":[{"@value":"In opmaak","@language":"nl-BE"}]},{"@id":"https://private-api.gipod.test-vlaanderen.be/api/v1/taxonomies/zonetypes/0fb72ef7-6ac9-4a70-b295-a30ea215d250","http://www.w3.org/2004/02/skos/core#prefLabel":[{"@value":"HinderZone","@language":"nl-BE"}]},{"@id":"https://schema.org/Schedule"},{"@id":"https://w3id.org/ldes#EventStream"},{"@id":"https://w3id.org/tree#Node"}]
					""")),
	})
	@ApiResponse(responseCode = "404", content = @Content, description = "No Linked Data Event Stream found with provided collection name")
	ResponseEntity<TreeNode> retrieveLdesFragment(HttpServletResponse response,
			@Parameter(example = "PaginationFragmentation") String view,
			@Parameter(examples = @ExampleObject(value = """
					{
						"fragment": "1"
					}
					""")) Map<String, String> requestParameters,
			@Parameter(hidden = true) String language,
			@Parameter(example = "mobility-hindrances") String collectionName);
}
