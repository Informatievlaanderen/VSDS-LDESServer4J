package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;

import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamResponseConverterImpl.DATASET_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.DC_TERMS_IDENTIFIER;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.RDF_SYNTAX_TYPE;
import static org.apache.jena.rdf.model.ModelFactory.createDefaultModel;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.util.ResourceUtils.renameResource;

public class DcatDataset {

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
		Model modelWithIdentity = ModelFactory.createDefaultModel();
		modelWithIdentity.add(model);
		modelWithIdentity.listStatements(null, RDF_SYNTAX_TYPE, createResource(DATASET_TYPE)).nextOptional()
				.ifPresent(statement -> addIdentityToModel(modelWithIdentity, statement, hostname));
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

	private void addIdentityToModel(Model modelWithIdentity, Statement statement, String hostname) {
		String datasetIriString = getDatasetIriString(hostname);
		renameResource(statement.getSubject(), datasetIriString);
		modelWithIdentity.add(createResource(datasetIriString), DC_TERMS_IDENTIFIER, datasetIriString);
	}

}
