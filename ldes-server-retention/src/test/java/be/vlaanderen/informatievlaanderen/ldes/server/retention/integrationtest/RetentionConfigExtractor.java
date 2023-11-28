package be.vlaanderen.informatievlaanderen.ldes.server.retention.integrationtest;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.RDFDataMgr;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.RETENTION_TYPE;

public class RetentionConfigExtractor {

    List<Model> readRetentionPolicyFromFile(String fileName) throws URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI().toString();
        return extractRetentionStatements(RDFDataMgr.loadModel(uri));
    }

    private List<Model> extractRetentionStatements(Model viewDescription) {
        List<Statement> statements = viewDescription.listStatements().toList();
        List<Model> retentionPolicies = new ArrayList<>();
        for (RDFNode retention : statements.stream()
                .filter(statement -> statement.getPredicate().toString().equals(RETENTION_TYPE))
                .map(Statement::getObject).toList()) {
            List<Statement> retentionStatements = retrieveAllRetentionStatements(retention, statements);
            Model retentionModel = ModelFactory.createDefaultModel();
            retentionModel.add(retentionStatements);
            retentionPolicies.add(retentionModel);
        }

        return retentionPolicies;
    }

    private List<Statement> retrieveAllRetentionStatements(RDFNode resource, List<Statement> statements) {
        List<Statement> statementList = new ArrayList<>();
        statements.stream()
                .filter(statement -> statement.getSubject().equals(resource))
                .forEach(statement -> {
                    statementList.add(statement);
                    if (statement.getObject().isResource()) {
                        statementList.addAll(retrieveAllRetentionStatements(statement.getResource(), statements));
                    }
                });
        return statementList;
    }

}
