package be.vlaanderen.informatievlaanderen.ldes.server.domain.view.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;

import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.jena.rdf.model.ResourceFactory.*;

public class DcatView {

	public static final String VIEW_DESCRIPTION_SUFFIX = "/description";
	public static final Property DCAT_DATA_SERVICE = createProperty("http://www.w3.org/ns/dcat#DataService");
	public static final Property DCAT_ENDPOINT_URL = createProperty("http://www.w3.org/ns/dcat#endpointURL");
	public static final Property DCAT_SERVES_DATASET = createProperty("http://www.w3.org/ns/dcat#servesDataset");

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

	public List<Statement> getStatementsWithBase(String hostName) {
		Resource dataServiceId = getDcat().listSubjectsWithProperty(RDF.type, DCAT_DATA_SERVICE).next().asResource();
		Resource viewDescriptionResource = getViewDescriptionResource(hostName);

		List<Statement> statements = getDcat().listStatements()
				.mapWith(stmnt -> stmnt.getSubject().equals(dataServiceId)
						? createStatement(viewDescriptionResource, stmnt.getPredicate(), stmnt.getObject())
						: stmnt)
				.toList();

		statements.add(createEndpointUrlStatement(viewDescriptionResource, hostName));
		statements.add(createServesDatasetStatement(viewDescriptionResource, hostName));

		return statements;
	}

	private Statement createEndpointUrlStatement(Resource dataServiceId, String hostName) {
		Resource view = createResource(getViewName().getViewNameIri(hostName));
		return ResourceFactory.createStatement(dataServiceId, DCAT_ENDPOINT_URL, view);
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
