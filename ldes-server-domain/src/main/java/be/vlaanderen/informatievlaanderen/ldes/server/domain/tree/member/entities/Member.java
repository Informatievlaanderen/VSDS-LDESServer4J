package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.FragmentationPropertyException;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_MEMBER;

public class Member {

	private final Model memberModel;
	private final String memberId;

	public Member(String memberId, final Model memberModel) {
		this.memberId = memberId;
		this.memberModel = memberModel;
	}

	public Model getModel() {
		return memberModel;
	}

	public Object getFragmentationObject(String fragmentationPredicateQuery) {
		// @formatter:off
        return getFragmentationObjects( fragmentationPredicateQuery)
				.stream()
				.findFirst()
				.orElseThrow(() -> new FragmentationPropertyException(memberId, fragmentationPredicateQuery));
        // @formatter:on
	}

	public List<Object> getFragmentationObjects(String fragmentationPropertyQuery) {
		Query query = QueryFactory.create(fragmentationPropertyQuery);

		try (QueryExecution qe = QueryExecution.create(query, memberModel)) {
			ResultSet rs = qe.execSelect();

			return ResultSetFormatter.toList(rs)
					.stream()
					.map(querySolution -> {
						List<RDFNode> rdfNodes = new ArrayList<>();
						query.getResultVars().forEach(resultVar -> rdfNodes.add(querySolution.get(resultVar)));
						return rdfNodes;
					})
					.flatMap(Collection::stream)
					.map(RDFNode::asLiteral)
					.map(Literal::getValue)
					.toList();
		}
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
