package be.vlaanderen.informatievlaanderen.ldes.server.domain.model;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;

import java.util.List;
import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.jena.rdf.model.ResourceFactory.*;
import static org.apache.jena.util.ResourceUtils.renameResource;

public class DcatView {

	public static final String VIEW_DESCRIPTION_SUFFIX = "/description";
	public static final Property DCAT_DATA_SERVICE = createProperty("http://www.w3.org/ns/dcat#DataService");
	public static final Property DCAT_ENDPOINT_URL = createProperty("http://www.w3.org/ns/dcat#endpointURL");
	public static final Property DCAT_ENDPOINT_DESCRIPTION = createProperty("http://www.w3.org/ns/dcat#endpointDescription");
	public static final Property DCAT_SERVES_DATASET = createProperty("http://www.w3.org/ns/dcat#servesDataset");
	public static final String RDF_SCHEMA = "http://www.w3.org/2000/01/rdf-schema#";
	public static final Property RDFS_RESOURCE = createProperty(RDF_SCHEMA, "Resource");



	private final ViewName viewName;
	private final Model dcat;

	private DcatView(ViewName viewName, Model dcat) {
		this.viewName = viewName;
		this.dcat = dcat;
	}

	public static DcatView from(ViewName viewName, Model dcat) {
		return new DcatView(notNull(viewName), notNull(dcat));
	}

	public ViewName getViewName() {
		return viewName;
	}

	public Model getDcat() {
		return dcat;
	}

	public List<Statement> getStatementsWithBase(String hostName, String swaggerUiPath) {
		Resource viewDescriptionResource = getViewDescriptionResource(hostName);

		final Model dcatWithIdentity = ModelFactory.createDefaultModel();
		dcatWithIdentity.add(getDcat());
		dcatWithIdentity.listStatements(null, RDF.type, DCAT_DATA_SERVICE).nextOptional()
				.ifPresent(statement -> renameResource(statement.getSubject(), viewDescriptionResource.getURI()));


		dcatWithIdentity.add(viewDescriptionResource, DC_TERMS_IDENTIFIER, createStringLiteral(viewName.getViewNameIri(hostName)));
		dcatWithIdentity.add(createEndpointUrlStatement(viewDescriptionResource, hostName));
		dcatWithIdentity.add(createEndpointDescriptionStatements(viewDescriptionResource, hostName, swaggerUiPath));
		dcatWithIdentity.add(createServesDatasetStatement(viewDescriptionResource, hostName));

		return dcatWithIdentity.listStatements().toList();
	}

	private Statement createEndpointUrlStatement(Resource dataServiceId, String hostName) {
		Resource view = createResource(getViewName().getViewNameIri(hostName));
		return ResourceFactory.createStatement(dataServiceId, DCAT_ENDPOINT_URL, view);
	}

	private List<Statement> createEndpointDescriptionStatements(Resource dataServiceId, String hostName, String swaggerUiPath) {
		String endpointDescription = "%s/%s?urls.primaryName=base".formatted(hostName, swaggerUiPath);
		return List.of(
				createStatement(dataServiceId, DCAT_ENDPOINT_DESCRIPTION, createProperty(endpointDescription)),
				createStatement(createProperty(endpointDescription), RDF_SYNTAX_TYPE, RDFS_RESOURCE)
		);
	}

	private Statement createServesDatasetStatement(Resource dataServiceId, String hostName) {
		Resource collection = createResource(viewName.getCollectionIri(hostName));
		return ResourceFactory.createStatement(dataServiceId, DCAT_SERVES_DATASET, collection);
	}

	public Resource getViewDescriptionResource(String hostName) {
		return createResource(viewName.getViewNameIri(hostName) + VIEW_DESCRIPTION_SUFFIX);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		DcatView dcatView = (DcatView) o;
		return viewName.equals(dcatView.viewName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(viewName);
	}
}
