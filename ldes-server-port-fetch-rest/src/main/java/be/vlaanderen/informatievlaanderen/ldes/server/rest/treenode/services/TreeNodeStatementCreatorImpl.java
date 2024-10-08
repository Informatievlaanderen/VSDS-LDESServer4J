package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.*;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;

@Component
public class TreeNodeStatementCreatorImpl implements TreeNodeStatementCreator {

    private final Map<String, Model> shaclShapes = new HashMap<>();
    private final Map<String, EventStream> eventStreams = new HashMap<>();
    private final Map<ViewName, DcatView> dcatViews = new HashMap<>();

    @Override
    public List<Statement> addEventStreamStatements(TreeNode treeNode, String baseUrl) {
	    Resource collectionId = createResource(baseUrl);
	    return new ArrayList<>(getEventStreamStatements(collectionId));
    }

    @Override
    public List<Statement> addTreeNodeStatements(TreeNode treeNode, String collectionName, String prefix) {
        EventStream eventStream = eventStreams.get(collectionName);
        if(eventStream == null) {
            throw new MissingResourceException("eventstream", collectionName);
        }
        Model shaclShape = shaclShapes.get(collectionName);
        List<TreeRelationResponse> treeRelationResponses = treeNode.getRelations().stream()
                .map(treeRelation -> new TreeRelationResponse(treeRelation.treePath(),
                        prefix + treeRelation.treeNode().asEncodedFragmentId(),
                        treeRelation.treeValue(), treeRelation.treeValueType(), treeRelation.relation()))
                .toList();
        TreeNodeInfoResponse treeNodeInfoResponse = new TreeNodeInfoResponse(prefix + treeNode.getFragmentId(), treeRelationResponses);
        List<Statement> statements = new ArrayList<>(treeNodeInfoResponse.convertToStatements());
        addLdesCollectionStatements(statements, treeNode.isView(), prefix + treeNode.getFragmentId(), eventStream, shaclShape, prefix);

        return statements;
    }

    private void addLdesCollectionStatements(List<Statement> statements, boolean isView, String currentFragmentId,
                                             EventStream eventStream, Model shaclShape, String prefix) {
        String baseUrl = prefix + "/" + eventStream.getCollection();
        Resource collection = createResource(baseUrl);

        if (isView) {
            EventStreamInfoResponse eventStreamInfoResponse = new EventStreamInfoResponse(
                    baseUrl,
                    eventStream.getTimestampPath(),
                    eventStream.getVersionOfPath(),
                    shaclShape,
                    Collections.singletonList(currentFragmentId));
            statements.addAll(eventStreamInfoResponse.convertToStatements());
            addDcatStatements(statements, currentFragmentId, eventStream.getCollection(), prefix);
        } else {
            statements.add(createStatement(createResource(currentFragmentId), IS_PART_OF_PROPERTY, collection));
        }
    }

    private void addDcatStatements(List<Statement> statements, String currentFragmentId, String collection, String prefix) {
        ViewName viewName = ViewName.fromString(currentFragmentId.substring(currentFragmentId.indexOf(collection)));
        DcatView dcatView = dcatViews.get(viewName);
        if (dcatView != null) {
            statements.addAll(dcatView.getStatementsWithBase(prefix));
        }
    }


    private List<Statement> getEventStreamStatements(Resource collectionId) {
        List<Statement> statements = new ArrayList<>();
        statements.add(createStatement(collectionId, RDF_SYNTAX_TYPE, createResource(LDES_EVENT_STREAM_URI)));
        return statements;
    }

    @EventListener
    public void handleShaclInitEvent(ShaclChangedEvent event) {
        shaclShapes.put(event.getCollection(), event.getModel());
    }

    @EventListener
    public void handleShaclDeletedEvent(ShaclDeletedEvent event) {
        shaclShapes.remove(event.collectionName());
    }

    @EventListener
    public void handleDcatViewSavedEvent(DcatViewSavedEvent event) {
        dcatViews.put(event.dcatView().getViewName(), event.dcatView());
    }

    @EventListener
    public void handleDcatViewDeletedEvent(DcatViewDeletedEvent event) {
        dcatViews.remove(event.viewName());
    }
    @EventListener
    public void handleEventStreamInitEvent(EventStreamCreatedEvent event) {
        eventStreams.put(event.eventStream().getCollection(), event.eventStream());
    }

    @EventListener
    public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
        eventStreams.remove(event.collectionName());

        dcatViews.keySet().stream()
                .filter(viewName -> viewName.getCollectionName().equals(event.collectionName()))
                .toList()
                .forEach(dcatViews::remove);
    }
}
