package be.vlaanderen.informatievlaanderen.ldes.server.ingest.skolemization;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.StatementImpl;

public class StatementBuilder {
	private final Property predicate;
	private Resource subject;
	private RDFNode object;

	private StatementBuilder(Property predicate) {
		this.predicate = predicate;
	}

	public Statement build() {
		return new StatementImpl(subject, predicate, object);
	}

	public StatementBuilder withSubject(Resource subject) {
		this.subject = subject;
		return this;
	}

	public StatementBuilder withObject(RDFNode object) {
		this.object = object;
		return this;
	}

	public static StatementBuilder withPredicate(Property predicate) {
		return new StatementBuilder(predicate);
	}

}
