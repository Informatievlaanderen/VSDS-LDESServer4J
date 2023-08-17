package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation.pointintime.PointInTimeRetentionPolicyCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation.timebased.TimeBasedRetentionPolicyCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation.versionbased.VersionBasedRetentionPolicyCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.RDF_SYNTAX_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation.RetentionPolicyConstants.*;

@Component
public class RetentionPolicyFactoryImpl implements RetentionPolicyFactory {

	private final Map<String, RetentionPolicyCreator> retentionPolicyCreatorMap;

	public RetentionPolicyFactoryImpl(MemberPropertiesRepository memberPropertiesRepository) {
		this.retentionPolicyCreatorMap = Map.of(
				TIME_BASED_RETENTION_POLICY, new TimeBasedRetentionPolicyCreator(),
				VERSION_BASED_RETENTION_POLICY, new VersionBasedRetentionPolicyCreator(memberPropertiesRepository),
				POINT_IN_TIME_RETENTION_POLICY, new PointInTimeRetentionPolicyCreator());
	}

	@Override
	public List<RetentionPolicy> getRetentionPolicyListForView(ViewSpecification viewSpecification) {
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
