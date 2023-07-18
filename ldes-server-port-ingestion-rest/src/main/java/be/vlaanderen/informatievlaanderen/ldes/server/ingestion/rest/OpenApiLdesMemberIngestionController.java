package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

import static org.apache.jena.riot.WebContent.*;

@Tag(name = "Ingest")
public interface OpenApiLdesMemberIngestionController {
	@Operation(summary = "Ingest version object to collection")
	void ingestLdesMember(
			@RequestBody(content = {
					@Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
							@prefix dc11: <http://purl.org/dc/elements/1.1/> .
							@prefix dc: <http://purl.org/dc/terms/> .
							@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
							@prefix ns0: <http://www.w3.org/ns/adms#> .
							@prefix skos: <http://www.w3.org/2004/02/skos/core#> .
							@prefix ns1: <https://gipod.vlaanderen.be/ns/gipod#> .
							@prefix prov: <http://www.w3.org/ns/prov#> .
							@prefix ns2: <https://data.vlaanderen.be/ns/mobiliteit#Inname.> .
							@prefix ns3: <https://data.vlaanderen.be/ns/mobiliteit#> .
							@prefix ns4: <http://data.europa.eu/m8g/> .
							@prefix ns5: <http://www.w3.org/ns/locn#> .
							@prefix ns6: <http://www.opengis.net/ont/geosparql#> .
							@prefix org: <http://www.w3.org/ns/org#> .

							<https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances?generatedAtTime=2022-05-20T09:58:15.867Z> a <https://w3id.org/tree#Node> .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1>
							  dc11:contributor <https://private-api.gipod.beta-vlaanderen.be/api/v1/organisations/a7eadbde-86a6-076c-bd2f-7f5596ba3730> ;
							  dc11:creator <https://private-api.gipod.beta-vlaanderen.be/api/v1/organisations/a7eadbde-86a6-076c-bd2f-7f5596ba3730> ;
							  dc:created "2022-05-20T09:58:15.8610896Z"^^xsd:dateTime ;
							  dc:description "omschrijving" ;
							  dc:isVersionOf <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464> ;
							  dc:modified "2022-05-20T09:58:15.8646433Z"^^xsd:dateTime ;
							  a <https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder> ;
							  ns0:identifier [
							    a ns0:Identifier ;
							    skos:notation "10810464"^^ns1:gipodId ;
							    ns0:schemaAgency "https://gipod.vlaanderen.be"@nl-be
							  ] ;
							  ns0:versionNotes "MobilityHindranceZoneWasAdded"@nl-be ;
							  prov:generatedAtTime "2022-05-20T09:58:15.867Z"^^xsd:dateTime ;
							  ns2:status <https://private-api.gipod.beta-vlaanderen.be/api/v1/taxonomies/statuses/0a4ee99b-8b8a-47c8-913f-117220febee0> ;
							  ns3:beheerder <https://private-api.gipod.beta-vlaanderen.be/api/v1/organisations/a7eadbde-86a6-076c-bd2f-7f5596ba3730> ;
							  ns3:periode [
							    ns4:endTime "2022-05-27T17:00:00Z"^^xsd:dateTime ;
							    ns4:startTime "2022-05-27T07:00:00Z"^^xsd:dateTime ;
							    a ns4:PeriodOfTime
							  ] ;
							  ns3:zone <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/zones/2af888b0-de0c-48c2-956b-8b36d01c1232> ;
							  ns1:gipodId 10810464 .

							<https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/zones/2af888b0-de0c-48c2-956b-8b36d01c1232>
							  a ns3:Zone ;
							  ns5:geometry [
							    ns6:asWKT "<http://www.opengis.net/def/crs/EPSG/9.9.1/31370> POLYGON ((170287.59 200940.75, 170287.57 200945.75, 170292.58 200945.77, 170292.6 200940.77, 170287.59 200940.75))"^^ns6:wktLiteral ;
							    a ns5:Geometry
							  ] ;
							  ns3:Zone.type <https://private-api.gipod.beta-vlaanderen.be/api/v1/taxonomies/zonetypes/0fb72ef7-6ac9-4a70-b295-a30ea215d250> ;
							  ns3:gevolg <https://private-api.gipod.beta-vlaanderen.be/api/v1/taxonomies/mobility-hindrance/consequencetypes/8eda1611-902b-4c9a-8b3c-4c23a49d7c5d> .

							<https://private-api.gipod.beta-vlaanderen.be/api/v1/organisations/a7eadbde-86a6-076c-bd2f-7f5596ba3730>
							  a org:Organization ;
							  skos:prefLabel "Gemeente Berlaar" .

							<https://private-api.gipod.beta-vlaanderen.be/api/v1/taxonomies/mobility-hindrance/consequencetypes/8eda1611-902b-4c9a-8b3c-4c23a49d7c5d> skos:prefLabel "Beperkte doorgang voor voetgangers"@nl-be .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/taxonomies/statuses/0a4ee99b-8b8a-47c8-913f-117220febee0> skos:prefLabel "In opmaak"@nl-be .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/taxonomies/zonetypes/0fb72ef7-6ac9-4a70-b295-a30ea215d250> skos:prefLabel "HinderZone"@nl-be .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/works/10810463>
							  a ns3:Werk ;
							  ns2:heeftGevolg <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> ;
							  ns1:gipodId 10810463 .
							""")),
					@Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances?generatedAtTime=2022-05-20T09:58:15.867Z> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/tree#Node> .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> <http://purl.org/dc/elements/1.1/contributor> <https://private-api.gipod.beta-vlaanderen.be/api/v1/organisations/a7eadbde-86a6-076c-bd2f-7f5596ba3730> .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> <http://purl.org/dc/elements/1.1/creator> <https://private-api.gipod.beta-vlaanderen.be/api/v1/organisations/a7eadbde-86a6-076c-bd2f-7f5596ba3730> .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> <http://purl.org/dc/terms/created> "2022-05-20T09:58:15.8610896Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> <http://purl.org/dc/terms/description> "omschrijving" .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> <http://purl.org/dc/terms/isVersionOf> <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464> .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> <http://purl.org/dc/terms/modified> "2022-05-20T09:58:15.8646433Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder> .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> <http://www.w3.org/ns/adms#identifier> _:b0 .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> <http://www.w3.org/ns/adms#versionNotes> "MobilityHindranceZoneWasAdded"@nl-be .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> <http://www.w3.org/ns/prov#generatedAtTime> "2022-05-20T09:58:15.867Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> <https://data.vlaanderen.be/ns/mobiliteit#Inname.status> <https://private-api.gipod.beta-vlaanderen.be/api/v1/taxonomies/statuses/0a4ee99b-8b8a-47c8-913f-117220febee0> .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> <https://data.vlaanderen.be/ns/mobiliteit#beheerder> <https://private-api.gipod.beta-vlaanderen.be/api/v1/organisations/a7eadbde-86a6-076c-bd2f-7f5596ba3730> .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> <https://data.vlaanderen.be/ns/mobiliteit#periode> _:b1 .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> <https://data.vlaanderen.be/ns/mobiliteit#zone> <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/zones/2af888b0-de0c-48c2-956b-8b36d01c1232> .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> <https://gipod.vlaanderen.be/ns/gipod#gipodId> "10810464"^^<http://www.w3.org/2001/XMLSchema#integer> .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/zones/2af888b0-de0c-48c2-956b-8b36d01c1232> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://data.vlaanderen.be/ns/mobiliteit#Zone> .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/zones/2af888b0-de0c-48c2-956b-8b36d01c1232> <http://www.w3.org/ns/locn#geometry> _:b2 .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/zones/2af888b0-de0c-48c2-956b-8b36d01c1232> <https://data.vlaanderen.be/ns/mobiliteit#Zone.type> <https://private-api.gipod.beta-vlaanderen.be/api/v1/taxonomies/zonetypes/0fb72ef7-6ac9-4a70-b295-a30ea215d250> .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/zones/2af888b0-de0c-48c2-956b-8b36d01c1232> <https://data.vlaanderen.be/ns/mobiliteit#gevolg> <https://private-api.gipod.beta-vlaanderen.be/api/v1/taxonomies/mobility-hindrance/consequencetypes/8eda1611-902b-4c9a-8b3c-4c23a49d7c5d> .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/organisations/a7eadbde-86a6-076c-bd2f-7f5596ba3730> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/org#Organization> .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/organisations/a7eadbde-86a6-076c-bd2f-7f5596ba3730> <http://www.w3.org/2004/02/skos/core#prefLabel> "Gemeente Berlaar" .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/taxonomies/mobility-hindrance/consequencetypes/8eda1611-902b-4c9a-8b3c-4c23a49d7c5d> <http://www.w3.org/2004/02/skos/core#prefLabel> "Beperkte doorgang voor voetgangers"@nl-be .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/taxonomies/statuses/0a4ee99b-8b8a-47c8-913f-117220febee0> <http://www.w3.org/2004/02/skos/core#prefLabel> "In opmaak"@nl-be .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/taxonomies/zonetypes/0fb72ef7-6ac9-4a70-b295-a30ea215d250> <http://www.w3.org/2004/02/skos/core#prefLabel> "HinderZone"@nl-be .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/works/10810463> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://data.vlaanderen.be/ns/mobiliteit#Werk> .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/works/10810463> <https://data.vlaanderen.be/ns/mobiliteit#Inname.heeftGevolg> <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> .
							<https://private-api.gipod.beta-vlaanderen.be/api/v1/works/10810463> <https://gipod.vlaanderen.be/ns/gipod#gipodId> "10810463"^^<http://www.w3.org/2001/XMLSchema#integer> .
							_:b0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/adms#Identifier> .
							_:b0 <http://www.w3.org/2004/02/skos/core#notation> "10810464"^^<https://gipod.vlaanderen.be/ns/gipod#gipodId> .
							_:b0 <http://www.w3.org/ns/adms#schemaAgency> "https://gipod.vlaanderen.be"@nl-be .
							_:b1 <http://data.europa.eu/m8g/endTime> "2022-05-27T17:00:00Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
							_:b1 <http://data.europa.eu/m8g/startTime> "2022-05-27T07:00:00Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
							_:b1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://data.europa.eu/m8g/PeriodOfTime> .
							_:b2 <http://www.opengis.net/ont/geosparql#asWKT> "<http://www.opengis.net/def/crs/EPSG/9.9.1/31370> POLYGON ((170287.59 200940.75, 170287.57 200945.75, 170292.58 200945.77, 170292.6 200940.77, 170287.59 200940.75))"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .
							_:b2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/locn#Geometry> .
							""")),
					@Content(mediaType = contentTypeJSONLD, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
							[{"@id":"_:b0","@type":["http://www.w3.org/ns/adms#Identifier"],"http://www.w3.org/2004/02/skos/core#notation":[{"@value":"10810464","@type":"https://gipod.vlaanderen.be/ns/gipod#gipodId"}],"http://www.w3.org/ns/adms#schemaAgency":[{"@value":"https://gipod.vlaanderen.be","@language":"nl-be"}]},{"@id":"_:b1","http://data.europa.eu/m8g/endTime":[{"@value":"2022-05-27T17:00:00Z","@type":"http://www.w3.org/2001/XMLSchema#dateTime"}],"http://data.europa.eu/m8g/startTime":[{"@value":"2022-05-27T07:00:00Z","@type":"http://www.w3.org/2001/XMLSchema#dateTime"}],"@type":["http://data.europa.eu/m8g/PeriodOfTime"]},{"@id":"_:b2","http://www.opengis.net/ont/geosparql#asWKT":[{"@value":"<http://www.opengis.net/def/crs/EPSG/9.9.1/31370> POLYGON ((170287.59 200940.75, 170287.57 200945.75, 170292.58 200945.77, 170292.6 200940.77, 170287.59 200940.75))","@type":"http://www.opengis.net/ont/geosparql#wktLiteral"}],"@type":["http://www.w3.org/ns/locn#Geometry"]},{"@id":"http://data.europa.eu/m8g/PeriodOfTime"},{"@id":"http://www.w3.org/ns/adms#Identifier"},{"@id":"http://www.w3.org/ns/locn#Geometry"},{"@id":"http://www.w3.org/ns/org#Organization"},{"@id":"https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder"},{"@id":"https://data.vlaanderen.be/ns/mobiliteit#Werk"},{"@id":"https://data.vlaanderen.be/ns/mobiliteit#Zone"},{"@id":"https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances?generatedAtTime=2022-05-20T09:58:15.867Z","@type":["https://w3id.org/tree#Node"]},{"@id":"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464"},{"@id":"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1","http://purl.org/dc/elements/1.1/contributor":[{"@id":"https://private-api.gipod.beta-vlaanderen.be/api/v1/organisations/a7eadbde-86a6-076c-bd2f-7f5596ba3730"}],"http://purl.org/dc/elements/1.1/creator":[{"@id":"https://private-api.gipod.beta-vlaanderen.be/api/v1/organisations/a7eadbde-86a6-076c-bd2f-7f5596ba3730"}],"http://purl.org/dc/terms/created":[{"@value":"2022-05-20T09:58:15.8610896Z","@type":"http://www.w3.org/2001/XMLSchema#dateTime"}],"http://purl.org/dc/terms/description":[{"@value":"omschrijving"}],"http://purl.org/dc/terms/isVersionOf":[{"@id":"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464"}],"http://purl.org/dc/terms/modified":[{"@value":"2022-05-20T09:58:15.8646433Z","@type":"http://www.w3.org/2001/XMLSchema#dateTime"}],"@type":["https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder"],"http://www.w3.org/ns/adms#identifier":[{"@id":"_:b0"}],"http://www.w3.org/ns/adms#versionNotes":[{"@value":"MobilityHindranceZoneWasAdded","@language":"nl-be"}],"http://www.w3.org/ns/prov#generatedAtTime":[{"@value":"2022-05-20T09:58:15.867Z","@type":"http://www.w3.org/2001/XMLSchema#dateTime"}],"https://data.vlaanderen.be/ns/mobiliteit#Inname.status":[{"@id":"https://private-api.gipod.beta-vlaanderen.be/api/v1/taxonomies/statuses/0a4ee99b-8b8a-47c8-913f-117220febee0"}],"https://data.vlaanderen.be/ns/mobiliteit#beheerder":[{"@id":"https://private-api.gipod.beta-vlaanderen.be/api/v1/organisations/a7eadbde-86a6-076c-bd2f-7f5596ba3730"}],"https://data.vlaanderen.be/ns/mobiliteit#periode":[{"@id":"_:b1"}],"https://data.vlaanderen.be/ns/mobiliteit#zone":[{"@id":"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/zones/2af888b0-de0c-48c2-956b-8b36d01c1232"}],"https://gipod.vlaanderen.be/ns/gipod#gipodId":[{"@value":10810464}]},{"@id":"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/zones/2af888b0-de0c-48c2-956b-8b36d01c1232","@type":["https://data.vlaanderen.be/ns/mobiliteit#Zone"],"http://www.w3.org/ns/locn#geometry":[{"@id":"_:b2"}],"https://data.vlaanderen.be/ns/mobiliteit#Zone.type":[{"@id":"https://private-api.gipod.beta-vlaanderen.be/api/v1/taxonomies/zonetypes/0fb72ef7-6ac9-4a70-b295-a30ea215d250"}],"https://data.vlaanderen.be/ns/mobiliteit#gevolg":[{"@id":"https://private-api.gipod.beta-vlaanderen.be/api/v1/taxonomies/mobility-hindrance/consequencetypes/8eda1611-902b-4c9a-8b3c-4c23a49d7c5d"}]},{"@id":"https://private-api.gipod.beta-vlaanderen.be/api/v1/organisations/a7eadbde-86a6-076c-bd2f-7f5596ba3730","@type":["http://www.w3.org/ns/org#Organization"],"http://www.w3.org/2004/02/skos/core#prefLabel":[{"@value":"Gemeente Berlaar"}]},{"@id":"https://private-api.gipod.beta-vlaanderen.be/api/v1/taxonomies/mobility-hindrance/consequencetypes/8eda1611-902b-4c9a-8b3c-4c23a49d7c5d","http://www.w3.org/2004/02/skos/core#prefLabel":[{"@value":"Beperkte doorgang voor voetgangers","@language":"nl-be"}]},{"@id":"https://private-api.gipod.beta-vlaanderen.be/api/v1/taxonomies/statuses/0a4ee99b-8b8a-47c8-913f-117220febee0","http://www.w3.org/2004/02/skos/core#prefLabel":[{"@value":"In opmaak","@language":"nl-be"}]},{"@id":"https://private-api.gipod.beta-vlaanderen.be/api/v1/taxonomies/zonetypes/0fb72ef7-6ac9-4a70-b295-a30ea215d250","http://www.w3.org/2004/02/skos/core#prefLabel":[{"@value":"HinderZone","@language":"nl-be"}]},{"@id":"https://private-api.gipod.beta-vlaanderen.be/api/v1/works/10810463","@type":["https://data.vlaanderen.be/ns/mobiliteit#Werk"],"https://data.vlaanderen.be/ns/mobiliteit#Inname.heeftGevolg":[{"@id":"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1"}],"https://gipod.vlaanderen.be/ns/gipod#gipodId":[{"@value":10810463}]},{"@id":"https://w3id.org/tree#Node"}]
							""")) }) Member member,
			@Parameter(name = "collectionname") String collectionName);
}
