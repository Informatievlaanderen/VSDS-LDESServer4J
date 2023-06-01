package be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.entity.DcatView;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;

public class DcatServer {

	public static final Property DCAT_CATALOG = createProperty("http://www.w3.org/ns/dcat#Catalog");
	public static final Property DCAT_SERVICE = createProperty("http://www.w3.org/ns/dcat#service");
	public static final Property DCAT_DATASET = createProperty("http://www.w3.org/ns/dcat#dataset");

	private final String id;
	private final Model dcat;

	public DcatServer(String id, Model dcat) {
		this.id = id;
		this.dcat = dcat;
	}

	public String getId() {
		return id;
	}

	public Model getDcat() {
		return dcat;
	}

	public List<Statement> getStatementsWithBase(String hostName, List<DcatView> dcatViews) {
		final List<Statement> statements = new ArrayList<>();
		statements.addAll(createCatalogStatements(hostName));
		statements.addAll(createDcatServiceStatements(hostName, dcatViews));
		// statements.addAll(createDcatDataSetStatements(hostName, dcatViews)); TODO
		// TVPJ

		return statements;
	}

	private List<Statement> createCatalogStatements(String hostName) {
		Resource dataServiceId = getDcat().listSubjectsWithProperty(RDF.type, DCAT_CATALOG).next().asResource();
		Resource serverResource = getServerResource(hostName);

		return getDcat().listStatements()
				.mapWith(stmnt -> stmnt.getSubject().equals(dataServiceId)
						? createStatement(serverResource, stmnt.getPredicate(), stmnt.getObject())
						: stmnt)
				.toList();
	}

	private List<Statement> createDcatServiceStatements(String hostName, List<DcatView> dcatViews) {
		Resource serverResource = getServerResource(hostName);
		return dcatViews
				.stream()
				.map(dcatView -> createStatement(serverResource, DCAT_SERVICE,
						dcatView.getViewDescriptionResource(hostName)))
				.toList();
	}

	public Resource getServerResource(String hostName) {
		return createResource(hostName);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		DcatServer that = (DcatServer) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
