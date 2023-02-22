package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.model;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringConfig;

public class LocalMemberSupplier {

	private final SubstringConfig config;

	public LocalMemberSupplier(SubstringConfig config) {
		this.config = config;
	}

	public LocalMember toLocalMember(Member member) {
		return new LocalMember(member, config.getFragmenterProperty(), config.getFragmenterSubjectFilter());
	}

}
