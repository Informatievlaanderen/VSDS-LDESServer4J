package be.vlaanderen.informatievlaanderen.ldes.server.domain.view.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;

import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;

public class DcatView {

	public static final Property DCAT_DATA_SERVICE = createProperty("http://www.w3.org/ns/dcat#DataService");

	private final ViewName viewName;
	private final Model dcat;

	private DcatView(ViewName viewName, Model dcat) {
		this.viewName = viewName;
		this.dcat = dcat;
	}

	public static DcatView from(ViewName viewName, Model dcat) {
		return new DcatView(notNull(viewName), notNull(dcat));
	}

	public ViewName getViewName() {
		return viewName;
	}

	public Model getDcat() {
		return dcat;
	}

	// TODO TVB: 26/05/2023 test
	public List<Statement> getStatementsWithBase(Resource viewDescription) {
		Resource dataServiceId = getDcat().listSubjectsWithProperty(RDF.type, DCAT_DATA_SERVICE).next().asResource();

		return getDcat().listStatements(dataServiceId, null, (RDFNode) null)
				.mapWith(stmnt -> createStatement(viewDescription, stmnt.getPredicate(), stmnt.getObject()))
				.toList();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		DcatView dcatView = (DcatView) o;
		return viewName.equals(dcatView.viewName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(viewName);
	}

}
