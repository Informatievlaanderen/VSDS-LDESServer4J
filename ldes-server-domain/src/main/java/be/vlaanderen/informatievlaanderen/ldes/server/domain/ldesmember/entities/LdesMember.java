package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities;

import org.apache.jena.rdf.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_MEMBER;

public class LdesMember {
	private static final Logger LOGGER = LoggerFactory.getLogger(LdesMember.class);

	private final Model memberModel;
	private final String memberId;

	public LdesMember(String memberId, final Model memberModel) {
		this.memberId = memberId;
		this.memberModel = memberModel;
	}

	public Model getModel() {
		return memberModel;
	}

	public Object getFragmentationObject(String fragmentationProperty) {
		// @formatter:off
        return memberModel
                .listStatements(null, ResourceFactory.createProperty(fragmentationProperty), (Resource) null)
                .nextOptional()
                .map(Statement::getObject)
                .map(RDFNode::asLiteral)
                .map(Literal::getValue)
                .orElseGet(() -> {
					LOGGER.error( "[ member: " + memberId + "] No properties were found for descriptor " + fragmentationProperty);
					return null;
				});
        // @formatter:on
	}

	public List<Object> getFragmentationObjects(String fragmentationProperty) {
		// @formatter:off
		return memberModel
				.listStatements(null, ResourceFactory.createProperty(fragmentationProperty), (Resource) null)
				.toList()
				.stream()
				.map(Statement::getObject)
				.map(RDFNode::asLiteral)
				.map(Literal::getValue)
				.collect(Collectors.collectingAndThen(Collectors.toList(), result -> {
					if (result.isEmpty()) {
						LOGGER.error( "[ member: " + memberId + "] No properties were found for descriptor " + fragmentationProperty);
					}
					return result;
				}));
		// @formatter:on
	}

	public String getLdesMemberId() {
		return memberId;
	}

	private Optional<Statement> getCurrentTreeMemberStatement() {
		return memberModel.listStatements(null, TREE_MEMBER, (Resource) null).nextOptional();
	}

	public void removeTreeMember() {
		getCurrentTreeMemberStatement().ifPresent(memberModel::remove);
	}
}
