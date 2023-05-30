package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.creation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.versionbased.VersionBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.Lang;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.LDES;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class VersionBasedRetentionPolicyCreator implements RetentionPolicyCreator {
	public static final Property LDES_AMOUNT = createProperty(LDES, "amount");
	private final MemberRepository memberRepository;

	public VersionBasedRetentionPolicyCreator(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Override
	public RetentionPolicy createRetentionPolicy(Model model) {
		List<RDFNode> ldesAmounts = model.listObjectsOfProperty(LDES_AMOUNT).toList();
		if (ldesAmounts.size() != 1) {
			throw new IllegalArgumentException(
					"Cannot Create Version Based Retention Policy in which there is not exactly 1 "
							+ LDES_AMOUNT.toString()
							+ " statement.\n Found " + ldesAmounts.size() + " statements in :\n"
							+ RdfModelConverter.toString(model, Lang.TURTLE));
		}
		return new VersionBasedRetentionPolicy(ldesAmounts.get(0).asLiteral().getInt(),
				memberRepository);
	}
}
