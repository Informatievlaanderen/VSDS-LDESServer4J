package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingLdesStreamException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.repository.LdesStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.LdesStreamModel;
import org.apache.jena.rdf.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class LdesStreamModelServiceImpl implements LdesStreamModelService {

	private final LdesStreamRepository repository;

	@Autowired
	public LdesStreamModelServiceImpl(LdesStreamRepository repository) {
		this.repository = repository;
	}

	@Override
	public String retrieveShape(String collectionName) {
		final String SHAPE = "shape";
		LdesStreamModel ldesStreamModel = repository.retrieveLdesStream(collectionName)
				.orElseThrow(() -> new MissingLdesStreamException(collectionName));

		return ldesStreamModel.getModel().listStatements(null,
				ResourceFactory.createProperty(SHAPE), (Resource) null).toList().stream().findFirst().get()
				.getObject().asLiteral().getString();

	}

	@Override
	public String updateShape(String collectionName) {
		final String SHAPE = "shape";
		LdesStreamModel ldesStreamModel = repository.retrieveLdesStream(collectionName)
				.orElseThrow(() -> new MissingLdesStreamException(collectionName));

		StmtIterator iterator = ldesStreamModel.getModel().listStatements(null, ResourceFactory.createProperty(SHAPE), (Resource) null);

		if(iterator.hasNext()) {
			Statement statement = iterator.nextStatement();
			ldesStreamModel.getModel().remove(statement);
		}

		// TODO: return updated collection name
		return null;
	}
}
