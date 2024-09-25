package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation.timebased.TimeBasedRetentionPolicyCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation.versionbased.VersionBasedRetentionPolicyCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timeandversionbased.TimeAndVersionBasedRetentionPolicy;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.RDF_SYNTAX_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation.RetentionPolicyConstants.TIME_BASED_RETENTION_POLICY;
import static be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation.RetentionPolicyConstants.VERSION_BASED_RETENTION_POLICY;

@Component
public class RetentionPolicyFactoryImpl implements RetentionPolicyFactory {

	private final Map<String, RetentionPolicyCreator> retentionPolicyCreatorMap;

	public RetentionPolicyFactoryImpl() {
		this.retentionPolicyCreatorMap = Map.of(
				TIME_BASED_RETENTION_POLICY, new TimeBasedRetentionPolicyCreator(),
				VERSION_BASED_RETENTION_POLICY, new VersionBasedRetentionPolicyCreator()
		);
	}

	@Override
	public Optional<RetentionPolicy> extractRetentionPolicy(ViewSpecification viewSpecification) {
		List<RetentionPolicy> policies = getRetentionPolicyListForView(viewSpecification);

		return createCombinedRetentionPolicy(policies);
	}

	@Override
	public Optional<RetentionPolicy> extractRetentionPolicy(List<Model> retentionPolicies) {
		List<RetentionPolicy> policies = retentionPolicies.stream().map(this::getRetentionPolicy).toList();

		return createCombinedRetentionPolicy(policies);
	}

	private Optional<RetentionPolicy> createCombinedRetentionPolicy(List<RetentionPolicy> policies) {
		return switch (policies.size()) {
			case 0 -> Optional.empty();
			case 1 -> Optional.of(policies.get(0));
			case 2 -> Optional.of(TimeAndVersionBasedRetentionPolicy.from(policies.get(0), policies.get(1)));
			default -> throw new IllegalArgumentException("A view cannot have more than 2 retention policies!");
		};
	}

	private List<RetentionPolicy> getRetentionPolicyListForView(ViewSpecification viewSpecification) {
		return viewSpecification
				.getRetentionConfigs()
				.stream()
				.map(this::getRetentionPolicy)
				.toList();
	}

	private RetentionPolicy getRetentionPolicy(Model retentionModel) {
		Optional<Statement> retentionTypeStatement = retentionModel
				.listStatements(null, RDF_SYNTAX_TYPE, (RDFNode) null).nextOptional();
		if (retentionTypeStatement.isEmpty()) {
			throw new IllegalArgumentException("Cannot Extract Retention Policy from statements:\n"
					+ RdfModelConverter.toString(retentionModel, Lang.TURTLE));
		}
		RetentionPolicyCreator retentionPolicyCreator = retentionPolicyCreatorMap
				.get(retentionTypeStatement.get().getObject().toString());
		if (retentionPolicyCreator == null) {
			throw new IllegalArgumentException(
					"Cannot Create Retention Policy from type: " + retentionTypeStatement.get().getObject().toString());
		}
		return retentionPolicyCreator.createRetentionPolicy(retentionModel);
	}
}
