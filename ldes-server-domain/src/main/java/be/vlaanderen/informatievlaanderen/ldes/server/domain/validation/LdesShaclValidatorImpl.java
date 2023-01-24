package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import org.apache.jena.graph.Graph;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;
import org.springframework.stereotype.Component;

@Component
public class LdesShaclValidatorImpl implements LdesShaclValidator {
	private Shapes shapes;

	public LdesShaclValidatorImpl(final LdesConfig ldesConfig) {
		if (ldesConfig.validation() != null) {
			if (ldesConfig.validation().isEnabled() && ldesConfig.validation().getShape() != null) {
				shapes = Shapes.parse(RDFDataMgr.loadGraph(ldesConfig.validation().getShape()));
			}
		}
	}

	@Override
	public boolean validate(Graph dataGraph) {
		if (shapes != null) {
			ValidationReport report = ShaclValidator.get().validate(shapes, dataGraph);

			if (!report.conforms()) {
				RDFDataMgr.write(System.err, report.getModel(), Lang.TTL);
			}

			return report.conforms();
		}
		return true;
	}
}
