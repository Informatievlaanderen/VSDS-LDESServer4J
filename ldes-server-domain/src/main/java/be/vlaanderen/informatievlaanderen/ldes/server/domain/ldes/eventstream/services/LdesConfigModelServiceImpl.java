package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingLdesConfigException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.repository.LdesConfigRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.LdesConfigModel;
import org.apache.jena.rdf.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.config.LdesAdminConstants.*;
import static org.apache.jena.rdf.model.ResourceFactory.*;

@Component
public class LdesConfigModelServiceImpl implements LdesConfigModelService {
	private final LdesConfigRepository repository;

	@Autowired
	public LdesConfigModelServiceImpl(LdesConfigRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<LdesConfigModel> retrieveAllEventStreams() {
		return repository.retrieveAllLdesStreams();
	}

	@Override
	public LdesConfigModel retrieveEventStream(String collectionName) {
		return repository.retrieveLdesStream(collectionName)
				.orElseThrow(() -> new MissingLdesConfigException(collectionName));
	}

	@Override
	public void deleteEventStream(String collectionName) {
		if (repository.retrieveLdesStream(collectionName).isEmpty()) {
			throw new MissingLdesConfigException(collectionName);
		}

		repository.deleteLdesStream(collectionName);
	}

	@Override
	public LdesConfigModel updateEventStream(LdesConfigModel ldesConfigModel) {
		return repository.saveLdesStream(ldesConfigModel);
	}

	@Override
	public Model retrieveShape(String collectionName) {
		LdesConfigModel ldesConfigModel = repository.retrieveLdesStream(collectionName)
				.orElseThrow(() -> new MissingLdesConfigException(collectionName));

		return ldesConfigModel.getModel().listStatements(null,
				createProperty(SHAPE), (Resource) null).toList().stream().findFirst()
				.map(Statement::getModel)
				.orElse(ModelFactory.createDefaultModel());
	}

	@Override
	public LdesConfigModel updateShape(String collectionName, LdesConfigModel shape) {
		LdesConfigModel ldesConfigModel = repository.retrieveLdesStream(collectionName)
				.orElseThrow(() -> new MissingLdesConfigException(collectionName));

		StmtIterator iterator = ldesConfigModel.getModel().listStatements(null, createProperty(SHAPE), (Resource) null);

		if (iterator.hasNext()) {
			Statement statement = iterator.nextStatement();
			List<Statement> statements = retrieveAllStatements(statement, ldesConfigModel.getModel());
			ldesConfigModel.getModel().remove(statements);
		}

		ldesConfigModel.getModel().add(shape.getModel());
		Statement statement = ldesConfigModel.getModel().createStatement(stringToResource(collectionName),
				createProperty(SHAPE), stringToResource(shape.getId()));
		ldesConfigModel.getModel().add(statement);
		repository.saveLdesStream(ldesConfigModel);

		return shape;
	}

	@Override
	public List<Model> retrieveViews(String collectionName) {
		LdesConfigModel ldesConfigModel = repository.retrieveLdesStream(collectionName)
				.orElseThrow(() -> new MissingLdesConfigException(collectionName));

		return ldesConfigModel.getModel().listStatements(null, createProperty(VIEW), (Resource) null)
				.toList().stream()
				.map(Statement::getResource)
				.map(resource -> ldesConfigModel.getModel().listStatements(resource, null, (Resource) null))
				.map(stmtIterator -> ModelFactory.createDefaultModel().add(stmtIterator))
				.toList();
	}

	@Override
	public LdesConfigModel addView(String collectionName, LdesConfigModel view) {
		LdesConfigModel ldesConfigModel = repository.retrieveLdesStream(collectionName)
				.orElseThrow(() -> new MissingLdesConfigException(collectionName));

		StmtIterator iterator = ldesConfigModel.getModel().listStatements(null, ResourceFactory.createProperty(VIEW),
				stringToResource(view.getId()));
		if (iterator.hasNext()) {
			Statement statement = iterator.nextStatement();
			List<Statement> statements = retrieveAllStatements(statement, ldesConfigModel.getModel());
			ldesConfigModel.getModel().remove(statements);
		}

		Statement viewStatement = ldesConfigModel.getModel().createStatement(stringToResource(collectionName),
				createProperty(VIEW), stringToResource(view.getId()));
		ldesConfigModel.getModel().add(viewStatement);
		ldesConfigModel.getModel().add(view.getModel());
		repository.saveLdesStream(ldesConfigModel);

		return view;
	}

	@Override
	public void deleteView(String collectionName, String viewName) {
		LdesConfigModel ldesConfigModel = repository.retrieveLdesStream(collectionName)
				.orElseThrow(() -> new MissingLdesConfigException(collectionName));

		StmtIterator iterator = ldesConfigModel.getModel().listStatements(stringToResource(collectionName),
				createProperty(VIEW), stringToResource(viewName));

		if (!iterator.hasNext()) {
			throw new MissingLdesConfigException("view", collectionName + "/" + viewName);
		}

		Statement statement = iterator.nextStatement();
		List<Statement> statements = retrieveAllStatements(statement, ldesConfigModel.getModel());
		ldesConfigModel.getModel().remove(statements);

		repository.saveLdesStream(ldesConfigModel);
	}

	@Override
	public Model retrieveView(String collectionName, String viewName) {
		LdesConfigModel ldesConfigModel = repository.retrieveLdesStream(collectionName)
				.orElseThrow(() -> new MissingLdesConfigException(collectionName));

		StmtIterator iterator = ldesConfigModel.getModel().listStatements(stringToResource(collectionName),
				createProperty(VIEW), stringToResource(viewName));

		if (!iterator.hasNext()) {
			throw new MissingLdesConfigException("view", collectionName + "/" + viewName);
		}

		// list of all the statements in the view
		List<Statement> viewStatements = retrieveAllStatements(stringToResource(viewName), ldesConfigModel.getModel());

		Model model = ModelFactory.createDefaultModel();
		model.add(viewStatements);

		return model;
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

	protected Resource stringToResource(String name) {
		return ResourceFactory.createResource(LDES + name);
	}
}
