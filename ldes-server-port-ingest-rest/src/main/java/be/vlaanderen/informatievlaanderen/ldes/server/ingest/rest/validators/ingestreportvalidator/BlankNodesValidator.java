package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.validators.ingestreportvalidator;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Order(1)
@Component
public class BlankNodesValidator implements IngestReportValidator {
    public void validate(Model model, EventStream eventStream, ShaclReportManager reportManager) {
        Map<Integer, List<Resource>> numberOfReferences = getNumberOfNodeReferences(model);

        validateDanglingBlankNodes(numberOfReferences, model, reportManager);
        validateBlankNodeScope(numberOfReferences, model, reportManager);
    }
    private void validateDanglingBlankNodes(Map<Integer, List<Resource>> nrOfReferences, Model model, ShaclReportManager reportManager) {
        if (nrOfReferences.containsKey(0)) {
            nrOfReferences.get(0).forEach(subject-> {
                if (subject.isAnon()) {
                    reportManager.addEntry(subject, model.listStatements(subject, null, (RDFNode) null).toList(),
                            "Object graphs don't allow blank nodes to occur outside of a named object.");
                }
            });
        }
    }

    private void validateBlankNodeScope(Map<Integer, List<Resource>> numberOfReferences, Model model, ShaclReportManager reportManager) {
        numberOfReferences.forEach((amount, resourceList) -> {
            if (amount > 1) {
                resourceList.forEach(resource -> reportManager.addEntry(resource,
                        model.listStatements(null, null, resource).toList(),
                        "Blank nodes must be scoped to one object."));
            }
        });

    }

    private Map<Integer, List<Resource>> getNumberOfNodeReferences(Model model) {
        return model.listSubjects().filterKeep(Resource::isAnon).toList().stream().collect(Collectors.groupingBy(s -> model.listStatements(null, null, s).mapWith(Statement::getSubject).toSet().size()));
    }
}
