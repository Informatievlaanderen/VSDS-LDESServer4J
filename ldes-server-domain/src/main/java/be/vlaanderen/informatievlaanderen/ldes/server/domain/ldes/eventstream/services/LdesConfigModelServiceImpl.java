package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingLdesStreamException;
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
				.orElseThrow(() -> new MissingLdesStreamException(collectionName));
	}

	@Override
	public LdesConfigModel updateEventStream(LdesConfigModel ldesConfigModel) {
		return repository.saveLdesStream(ldesConfigModel);
	}

	@Override
	public Model retrieveShape(String collectionName) {
		LdesConfigModel ldesConfigModel = repository.retrieveLdesStream(collectionName)
				.orElseThrow(() -> new MissingLdesStreamException(collectionName));

		return ldesConfigModel.getModel().listStatements(null,
				createProperty(SHAPE), (Resource) null).toList().stream().findFirst()
				.map(Statement::getModel)
				.orElse(ModelFactory.createDefaultModel());
	}

	@Override
	public LdesConfigModel updateShape(String collectionName, LdesConfigModel shape) {
		LdesConfigModel ldesConfigModel = repository.retrieveLdesStream(collectionName)
				.orElseThrow(() -> new MissingLdesStreamException(collectionName));

		StmtIterator iterator = ldesConfigModel.getModel().listStatements(null, createProperty(SHAPE), (Resource) null);

		if (iterator.hasNext()) {
			Statement statement = iterator.nextStatement();
			Resource resource = statement.getResource();

			List<Statement> statements = retrieveAllStatements(resource, ldesConfigModel.getModel());
			statements.add(statement);
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
				.orElseThrow(() -> new MissingLdesStreamException(collectionName));

		ldesConfigModel.getModel().listStatements();

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
				.orElseThrow(() -> new MissingLdesStreamException(collectionName));

		StmtIterator iterator = ldesConfigModel.getModel().listStatements(null, ResourceFactory.createProperty(VIEW),
				stringToResource(view.getId()));
		if (iterator.hasNext()) {
			// TODO: view may have to be updated
		} else {
			ldesConfigModel.getModel().add(view.getModel());
			Statement statement = ldesConfigModel.getModel().createStatement(stringToResource(collectionName),
					createProperty(VIEW), stringToResource(ldesConfigModel.getId()));
			ldesConfigModel.getModel().add(statement);
			repository.saveLdesStream(ldesConfigModel);
		}

		return view;
	}

	@Override
	public Model retrieveView(String collectionName, String viewName) {
		Resource resource = stringToResource(viewName);

		LdesConfigModel ldesConfigModel = repository.retrieveLdesStream(collectionName)
				.orElseThrow(() -> new MissingLdesStreamException(collectionName));

		List<Statement> statements = retrieveAllStatements(resource, ldesConfigModel.getModel());

		Model model = ModelFactory.createDefaultModel();
		model.add(statements);

		return model;
	}

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

	protected Resource stringToResource(String name) {
		return ResourceFactory.createResource(LDES + name);
	}
}
