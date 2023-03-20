package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingLdesStreamException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.repository.LdesStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.LdesStreamModel;
import org.apache.jena.rdf.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.config.LdesAdminConstants.*;
import static org.apache.jena.rdf.model.ResourceFactory.*;

@Component
public class LdesStreamModelServiceImpl implements LdesStreamModelService {
	private final LdesStreamRepository repository;

	@Autowired
	public LdesStreamModelServiceImpl(LdesStreamRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<LdesStreamModel> retrieveAllEventStreams() {
		return repository.retrieveAllLdesStreams();
	}

	@Override
	public LdesStreamModel retrieveEventStream(String collectionName) {
		return repository.retrieveLdesStream(collectionName)
				.orElseThrow(() -> new MissingLdesStreamException(collectionName));
	}

	@Override
	public LdesStreamModel updateEventStream(LdesStreamModel ldesStreamModel) {
		return repository.saveLdesStream(ldesStreamModel);
	}

	@Override
	public Model retrieveShape(String collectionName) {
		LdesStreamModel ldesStreamModel = repository.retrieveLdesStream(collectionName)
				.orElseThrow(() -> new MissingLdesStreamException(collectionName));

		return ldesStreamModel.getModel().listStatements(null,
				createProperty(SHAPE), (Resource) null).toList().stream().findFirst()
				.map(Statement::getModel)
				.orElse(ModelFactory.createDefaultModel());
	}

	@Override
	public LdesStreamModel updateShape(String collectionName, LdesStreamModel shape) {
		LdesStreamModel ldesStreamModel = repository.retrieveLdesStream(collectionName)
				.orElseThrow(() -> new MissingLdesStreamException(collectionName));

		StmtIterator iterator = ldesStreamModel.getModel().listStatements(null, createProperty(SHAPE), (Resource) null);

		if (iterator.hasNext()) {
			Statement statement = iterator.nextStatement();
			Resource resource = statement.getResource();

			List<Statement> statements = retrieveAllStatements(resource, ldesStreamModel.getModel());
			statements.add(statement);
			ldesStreamModel.getModel().remove(statements);
		}

		ldesStreamModel.getModel().add(shape.getModel());
		Statement statement = ldesStreamModel.getModel().createStatement(stringToResource(collectionName),
				createProperty(SHAPE), stringToResource(shape.getId()));
		ldesStreamModel.getModel().add(statement);
		repository.saveLdesStream(ldesStreamModel);

		return shape;
	}

	@Override
	public List<Model> retrieveViews(String collectionName) {
		LdesStreamModel ldesStreamModel = repository.retrieveLdesStream(collectionName)
				.orElseThrow(() -> new MissingLdesStreamException(collectionName));

		ldesStreamModel.getModel().listStatements();

		return ldesStreamModel.getModel().listStatements(null, createProperty(VIEW), (Resource) null)
				.toList().stream()
				.map(Statement::getResource)
				.map(resource -> ldesStreamModel.getModel().listStatements(resource, null, (Resource) null))
				.map(stmtIterator -> ModelFactory.createDefaultModel().add(stmtIterator))
				.toList();
	}

	@Override
	public LdesStreamModel addView(String collectionName, LdesStreamModel view) {
		LdesStreamModel ldesStreamModel = repository.retrieveLdesStream(collectionName)
				.orElseThrow(() -> new MissingLdesStreamException(collectionName));

		StmtIterator iterator = ldesStreamModel.getModel().listStatements(null, ResourceFactory.createProperty(VIEW),
				stringToResource(view.getId()));
		if (iterator.hasNext()) {
			// TODO: view may have to be updated
		} else {
			ldesStreamModel.getModel().add(view.getModel());
			Statement statement = ldesStreamModel.getModel().createStatement(stringToResource(collectionName),
					createProperty(VIEW), stringToResource(ldesStreamModel.getId()));
			ldesStreamModel.getModel().add(statement);
			repository.saveLdesStream(ldesStreamModel);
		}

		return view;
	}

	@Override
	public Model retrieveView(String collectionName, String viewName) {
		Resource resource = stringToResource(viewName);

		LdesStreamModel ldesStreamModel = repository.retrieveLdesStream(collectionName)
				.orElseThrow(() -> new MissingLdesStreamException(collectionName));

		List<Statement> statements = retrieveAllStatements(resource, ldesStreamModel.getModel());

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
