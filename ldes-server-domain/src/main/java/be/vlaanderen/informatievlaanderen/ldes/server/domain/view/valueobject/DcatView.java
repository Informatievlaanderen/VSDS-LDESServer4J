package be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import java.io.StringWriter;
import java.util.Objects;

import static org.apache.commons.lang3.Validate.notNull;

public class DcatView {

	private final ViewName viewName;
	private final Model dcat;

	private DcatView(ViewName viewName, Model dcat) {
		this.viewName = viewName;
		this.dcat = dcat;
	}

	public static DcatView from(ViewName viewName, Model dcat) {
		return new DcatView(notNull(viewName), dcat);
	}

	public ViewName getViewName() {
		return viewName;
	}

	public Model getDcat() {
		return dcat;
	}

	public String getViewNameAsString() {
		return viewName.asString();
	}

	public String getDcatAsString() {
		final StringWriter outputStream = new StringWriter();
		RDFDataMgr.write(outputStream, dcat, Lang.NQUADS);
		return outputStream.toString();
	}

	// TODO: 25/05/2023 test
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DcatView dcatView = (DcatView) o;
		return viewName.equals(dcatView.viewName) && Objects.equals(dcat, dcatView.dcat);
	}

	@Override
	public int hashCode() {
		return Objects.hash(viewName, dcat);
	}

}
