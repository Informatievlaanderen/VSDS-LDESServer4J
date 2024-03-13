package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities;

import org.apache.jena.rdf.model.*;

import java.util.List;
import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamTOConverterImpl.DATASET_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static org.apache.jena.rdf.model.ModelFactory.createDefaultModel;
import static org.apache.jena.rdf.model.ResourceFactory.*;
import static org.apache.jena.util.ResourceUtils.renameResource;

public class DcatDataset {
	public static final Property TREE_SPECIFICATION = createProperty("https://w3id.org/tree/specification");
	public static final Property LDES_SPECIFICATION = createProperty("https://w3id.org/ldes/specification");

	private final String collectionName;
	private final Model model;

	public DcatDataset(String collectionName, Model model) {
		this.collectionName = collectionName;
		this.model = model;
	}

	public DcatDataset(String collectionName) {
		this(collectionName, createDefaultModel());
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
		modelWithIdentity.listStatements(null, RDF_SYNTAX_TYPE, createResource(DATASET_TYPE)).nextOptional()
				.ifPresent(statement -> renameResource(statement.getSubject(), datasetIriString));
		modelWithIdentity.add(createResource(datasetIriString), DC_TERMS_IDENTIFIER,
				modelWithIdentity.createTypedLiteral(datasetIriString, RDF_LITERAL));
		modelWithIdentity.add(createConformsToStatements(createResource(datasetIriString)));
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
				createStatement(datasetIri, DC_CONFORMS_TO, TREE_SPECIFICATION),
				createStatement(datasetIri, DC_CONFORMS_TO, LDES_SPECIFICATION),
				createStatement(TREE_SPECIFICATION, RDF_SYNTAX_TYPE, DC_STANDARD),
				createStatement(LDES_SPECIFICATION, RDF_SYNTAX_TYPE, DC_STANDARD)
		);
	}
}
