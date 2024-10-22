package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.DcatView;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.ResourceUtils;
import org.apache.jena.vocabulary.RDF;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DcatServer {

	public static final Property DCAT_CATALOG = ResourceFactory.createProperty("http://www.w3.org/ns/dcat#Catalog");
	public static final Property DCAT_SERVICE = ResourceFactory.createProperty("http://www.w3.org/ns/dcat#service");
	public static final Property DCAT_DATASET = ResourceFactory.createProperty("http://www.w3.org/ns/dcat#dataset");

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

	public List<Statement> getStatementsWithBase(String hostName, List<DcatView> dcatViews,
	                                             List<DcatDataset> datasets) {
		final List<Statement> statements = new ArrayList<>();
		statements.add(createIdentifierStatement(hostName));
		statements.addAll(createCatalogStatements(hostName));
		statements.addAll(createDcatServiceStatements(hostName, dcatViews));
		statements.addAll(createDcatDataSetStatements(hostName, datasets));
		return statements;
	}

	private Statement createIdentifierStatement(String hostName) {
		Resource subject = getServerResource(hostName);
		return ResourceFactory.createStatement(subject, RdfConstants.DC_TERMS_IDENTIFIER, dcat.createTypedLiteral(id, RdfConstants.RDF_LITERAL));
	}

	private List<Statement> createCatalogStatements(String hostName) {
		final Model dcatWithIdentity = ModelFactory.createDefaultModel();
		dcatWithIdentity.add(getDcat());
		dcatWithIdentity.listStatements(null, RDF.type, DCAT_CATALOG).nextOptional()
				.ifPresent(statement -> ResourceUtils.renameResource(statement.getSubject(), getServerResource(hostName).getURI()));
		return dcatWithIdentity.listStatements().toList();
	}

	private List<Statement> createDcatServiceStatements(String hostName, List<DcatView> dcatViews) {
		Resource serverResource = getServerResource(hostName);
		return dcatViews
				.stream()
				.map(dcatView -> ResourceFactory.createStatement(serverResource, DCAT_SERVICE,
						dcatView.getViewDescriptionResource(hostName)))
				.toList();
	}

	private List<Statement> createDcatDataSetStatements(String hostName, List<DcatDataset> datasets) {
		Resource serverResource = getServerResource(hostName);
		return datasets
				.stream()
				.map(dataset -> ResourceFactory.createStatement(serverResource, DCAT_DATASET,
						ResourceFactory.createResource(dataset.getDatasetIriString(hostName))))
				.toList();
	}

	public Resource getServerResource(String hostName) {
		return ResourceFactory.createResource(hostName);
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
