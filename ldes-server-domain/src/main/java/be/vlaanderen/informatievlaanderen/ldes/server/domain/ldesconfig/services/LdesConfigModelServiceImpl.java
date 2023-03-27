package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingLdesConfigException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.repository.LdesConfigRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;
import org.apache.jena.rdf.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static org.apache.jena.rdf.model.ResourceFactory.*;

@Component
public class LdesConfigModelServiceImpl implements LdesConfigModelService {
	private final LdesConfigRepository repository;

	@Autowired
	public LdesConfigModelServiceImpl(LdesConfigRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<LdesConfigModel> retrieveAllConfigModels() {
		return repository.retrieveAllConfigModels();
	}

	@Override
	public LdesConfigModel retrieveConfigModel(String collectionName) {
		return repository.retrieveConfigModel(collectionName)
				.orElseThrow(() -> new MissingLdesConfigException(collectionName));
	}

	@Override
	public void deleteConfigModel(String collectionName) {
		if (repository.retrieveConfigModel(collectionName).isEmpty()) {
			throw new MissingLdesConfigException(collectionName);
		}

		repository.deleteConfigModel(collectionName);
	}

	@Override
	public LdesConfigModel updateConfigModel(LdesConfigModel ldesConfigModel) {
		return repository.saveConfigModel(ldesConfigModel);
	}

	@Override
	public LdesConfigModel retrieveShape(String collectionName) {
		LdesConfigModel ldesConfigModel = retrieveConfigModel(collectionName);

		Model shapeModel = ldesConfigModel.getModel().listStatements(null,
				createProperty(SHAPE), (Resource) null).toList().stream().findFirst()
				.map(statement -> retrieveAllStatements(statement.getResource(), ldesConfigModel.getModel()))
				.map(statements -> {
					Model model = ModelFactory.createDefaultModel();
					model.add(statements);
					return model;
				})
				.orElse(ModelFactory.createDefaultModel());
		return LdesConfigModel.createLdesConfigShape(collectionName, shapeModel);
	}

	@Override
	public LdesConfigModel updateShape(String collectionName, LdesConfigModel shape) {
		LdesConfigModel ldesConfigModel = retrieveConfigModel(collectionName);

		StmtIterator iterator = ldesConfigModel.getModel().listStatements(null, createProperty(SHAPE), (Resource) null);

		if (iterator.hasNext()) {
			Statement statement = iterator.nextStatement();
			List<Statement> statements = retrieveAllStatements(statement, ldesConfigModel.getModel());
			ldesConfigModel.getModel().remove(statements);
		}

		ldesConfigModel.getModel().add(shape.getModel());
		Statement statement = ldesConfigModel.getModel().createStatement(idStringToResource(collectionName),
				createProperty(SHAPE), idStringToResource(shape.getId()));
		ldesConfigModel.getModel().add(statement);
		repository.saveConfigModel(ldesConfigModel);

		return shape;
	}

	@Override
	public List<LdesConfigModel> retrieveViews(String collectionName) {
		LdesConfigModel ldesConfigModel = retrieveConfigModel(collectionName);

		return ldesConfigModel.getModel().listStatements(null, createProperty(VIEW), (Resource) null)
				.toList().stream()
				.map(Statement::getResource)
				.map(resource -> {
					List<Statement> statements = retrieveAllStatements(resource, ldesConfigModel.getModel());
					Model viewModel = ModelFactory.createDefaultModel().add(statements);
					String name = resource.toString();
					return new LdesConfigModel(name, viewModel);
				})
				.toList();
	}

	@Override
	public LdesConfigModel addView(String collectionName, LdesConfigModel view) {
		LdesConfigModel ldesConfigModel = retrieveConfigModel(collectionName);

		StmtIterator iterator = ldesConfigModel.getModel().listStatements(null, ResourceFactory.createProperty(VIEW),
				idStringToResource(view.getId()));
		if (iterator.hasNext()) {
			Statement statement = iterator.nextStatement();
			List<Statement> statements = retrieveAllStatements(statement, ldesConfigModel.getModel());
			ldesConfigModel.getModel().remove(statements);
		}

		Statement viewStatement = ldesConfigModel.getModel().createStatement(idStringToResource(collectionName),
				createProperty(VIEW), idStringToResource(view.getId()));
		ldesConfigModel.getModel().add(viewStatement);
		ldesConfigModel.getModel().add(view.getModel());
		repository.saveConfigModel(ldesConfigModel);

		return view;
	}

	@Override
	public void deleteView(String collectionName, String viewName) {
		LdesConfigModel ldesConfigModel = retrieveConfigModel(collectionName);

		StmtIterator iterator = ldesConfigModel.getModel().listStatements(idStringToResource(collectionName),
				createProperty(VIEW), idStringToResource(viewName));

		if (!iterator.hasNext()) {
			throw new MissingLdesConfigException(collectionName, viewName);
		}

		Statement statement = iterator.nextStatement();
		List<Statement> statements = retrieveAllStatements(statement, ldesConfigModel.getModel());
		ldesConfigModel.getModel().remove(statements);

		repository.saveConfigModel(ldesConfigModel);
	}

	@Override
	public LdesConfigModel retrieveView(String collectionName, String viewName) {
		LdesConfigModel ldesConfigModel = retrieveConfigModel(collectionName);

		StmtIterator iterator = ldesConfigModel.getModel().listStatements(idStringToResource(collectionName),
				createProperty(VIEW), idStringToResource(viewName));

		if (!iterator.hasNext()) {
			throw new MissingLdesConfigException(collectionName, viewName);
		}

		// list of all the statements in the view
		List<Statement> viewStatements = retrieveAllStatements(idStringToResource(viewName),
				ldesConfigModel.getModel());

		Model model = ModelFactory.createDefaultModel();
		model.add(viewStatements);

		return new LdesConfigModel(viewName, model);
	}

	/**
	 * @param resource
	 *            the resource of which the according statements need to be
	 *            retrieved
	 * @param model
	 *            the model of which all the statements need to be retrieved
	 * @return a list of all the according statement of the model
	 */
	private List<Statement> retrieveAllStatements(Resource resource, Model model) {
		StmtIterator iterator = model.listStatements(resource, null, (Resource) null);
		List<Statement> statements = new ArrayList<>();

		while (iterator.hasNext()) {
			Statement statement = iterator.nextStatement();
			statements.add(statement);

			if (statement.getObject().isResource()) {
				statements.addAll(retrieveAllStatements(statement.getResource(), model));
			}
		}

		return statements;
	}

	/**
	 * @param statement
	 *            the statement of which the according statements need to be
	 *            retrieved
	 * @param model
	 *            the model of which all the statements need to be retrieved
	 * @return a list of all the according statement of the model
	 */
	private List<Statement> retrieveAllStatements(Statement statement, Model model) {
		List<Statement> statements = retrieveAllStatements(statement.getResource(), model);
		statements.add(statement);
		return statements;
	}

	protected Resource idStringToResource(String name) {
		// the LDES constant is meant to be the local prefix
		return ResourceFactory.createResource(LDES + name);
	}

	public static String extractIdFromResource(Resource resource) {
		// the LDES constant is meant to be the local prefix
		return resource.toString().replace(LDES, "");
	}
}
