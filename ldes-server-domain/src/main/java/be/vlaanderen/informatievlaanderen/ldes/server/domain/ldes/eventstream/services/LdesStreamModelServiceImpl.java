package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingLdesStreamException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.repository.LdesStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.LdesStreamModel;
import org.apache.jena.rdf.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.config.LdesAdminConstants.*;
import static org.apache.jena.rdf.model.ResourceFactory.*;

@Component
public class LdesStreamModelServiceImpl implements LdesStreamModelService {
    private final LdesStreamRepository streamRepository;
    @Autowired
    public LdesStreamModelServiceImpl(LdesStreamRepository streamRepository) {
        this.streamRepository = streamRepository;
    }

    @Override
    public String retrieveShape(String collectionName) {
        LdesStreamModel ldesStreamModel = streamRepository.retrieveLdesStream(collectionName)
                .orElseThrow(() -> new MissingLdesStreamException(collectionName));

        return ldesStreamModel.getModel().listStatements(null,
                        createProperty(SHAPE), (Resource) null).toList().stream().findFirst().get()
                .getObject().asLiteral().getString();

    }

    @Override
    public String updateShape(String collectionName, String shape) {
        LdesStreamModel ldesStreamModel = streamRepository.retrieveLdesStream(collectionName)
                .orElseThrow(() -> new MissingLdesStreamException(collectionName));

        ldesStreamModel.getModel().listStatements(null, createProperty(SHAPE), (Resource) null).remove();
        ldesStreamModel.getModel().createStatement(createResource(collectionName), createProperty(SHAPE), shape);
        streamRepository.saveLdesStream(ldesStreamModel);

        return shape;
    }

    @Override
    public List<Model> retrieveViews(String collectionName) {
        LdesStreamModel ldesStreamModel = streamRepository.retrieveLdesStream(collectionName)
                .orElseThrow(() -> new MissingLdesStreamException(collectionName));

        ldesStreamModel.getModel().listStatements();

        return ldesStreamModel.getModel().listStatements(null, createProperty(VIEW), (Resource) null)
                .toList().stream().map(Statement::getResource)
                .map(resource -> ldesStreamModel.getModel().listStatements(resource, null, (Resource) null))
                .map(stmtIterator -> ModelFactory.createDefaultModel().add(stmtIterator))
                .toList();
    }

    @Override
    public LdesStreamModel addView(String collectionName, LdesStreamModel view) {
        LdesStreamModel ldesStreamModel = streamRepository.retrieveLdesStream(collectionName)
                .orElseThrow(() -> new MissingLdesStreamException(collectionName));

        StmtIterator iterator = ldesStreamModel.getModel().listStatements(null, ResourceFactory.createProperty(VIEW), stringToResource(view.getId()));
        if(iterator.hasNext()) {
            // TODO: view may have to be updated
        } else {
            ldesStreamModel.getModel().add(view.getModel());
            streamRepository.saveLdesStream(ldesStreamModel);
        }

        return view;
    }

    @Override
    public Model retrieveView(String collectionName, String viewName) {
        Resource resource = ResourceFactory.createResource(viewName);

        LdesStreamModel ldesStreamModel = streamRepository.retrieveLdesStream(collectionName)
                .orElseThrow(() -> new MissingLdesStreamException(collectionName));

        var iterator = ldesStreamModel.getModel().listStatements(resource, null, (Resource) null);
        Model model = ModelFactory.createDefaultModel();
        model.add(iterator);

        return model;
    }

    protected Resource stringToResource(String name) {
        return ResourceFactory.createResource(NAME_PREFIX + name);
    }
}
