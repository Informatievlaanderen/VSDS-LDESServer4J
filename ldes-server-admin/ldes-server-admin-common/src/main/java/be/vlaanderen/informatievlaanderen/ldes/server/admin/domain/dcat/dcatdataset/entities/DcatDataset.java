package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.ResourceUtils;

import java.util.List;
import java.util.Objects;

public class DcatDataset {
	public static final String DCAT_PREFIX = "http://www.w3.org/ns/dcat#";
	public static final String DATASET_TYPE = DCAT_PREFIX + "Dataset";
	public static final Property TREE_SPECIFICATION = ResourceFactory.createProperty("https://w3id.org/tree/specification");
	public static final Property LDES_SPECIFICATION = ResourceFactory.createProperty("https://w3id.org/ldes/specification");

	private final String collectionName;
	private final Model model;

	public DcatDataset(String collectionName, Model model) {
		this.collectionName = collectionName;
		this.model = model;
	}

	public DcatDataset(String collectionName) {
		this(collectionName, ModelFactory.createDefaultModel());
	}

	public String getCollectionName() {
		return collectionName;
	}

	public Model getModel() {
		return model;
	}

	public Model getModelWithIdentity(String hostname) {
		String datasetIriString = getDatasetIriString(hostname);
		Model modelWithIdentity = ModelFactory.createDefaultModel();
		modelWithIdentity.add(model);
		modelWithIdentity.listStatements(null, RdfConstants.RDF_SYNTAX_TYPE, ResourceFactory.createResource(DATASET_TYPE)).nextOptional()
				.ifPresent(statement -> ResourceUtils.renameResource(statement.getSubject(), datasetIriString));
		modelWithIdentity.add(ResourceFactory.createResource(datasetIriString), RdfConstants.DC_TERMS_IDENTIFIER,
				modelWithIdentity.createTypedLiteral(datasetIriString, RdfConstants.RDF_LITERAL));
		modelWithIdentity.add(createConformsToStatements(ResourceFactory.createResource(datasetIriString)));
		return modelWithIdentity;
	}

	public String getDatasetIriString(String hostName) {
		return hostName + "/" + getCollectionName();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		DcatDataset that = (DcatDataset) o;
		return Objects.equals(collectionName, that.getCollectionName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(collectionName);
	}

	private List<Statement> createConformsToStatements(Resource datasetIri) {
		return List.of(
				ResourceFactory.createStatement(datasetIri, RdfConstants.DC_CONFORMS_TO, TREE_SPECIFICATION),
				ResourceFactory.createStatement(datasetIri, RdfConstants.DC_CONFORMS_TO, LDES_SPECIFICATION),
				ResourceFactory.createStatement(TREE_SPECIFICATION, RdfConstants.RDF_SYNTAX_TYPE, RdfConstants.DC_STANDARD),
				ResourceFactory.createStatement(LDES_SPECIFICATION, RdfConstants.RDF_SYNTAX_TYPE, RdfConstants.DC_STANDARD)
		);
	}
}
