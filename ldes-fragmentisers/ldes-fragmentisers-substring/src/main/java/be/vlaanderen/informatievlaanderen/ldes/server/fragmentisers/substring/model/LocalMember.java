package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.model;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.apicatalog.jsonld.StringUtils;

public class LocalMember extends Member {

	private final String fragmenterProperty;
	private final String fragmenterSubjectFilter;

	public LocalMember(Member member, String fragmenterProperty, String fragmenterSubjectFilter) {
		super(member.getLdesMemberId(), member.getModel(), member.getTreeNodeReferences());

		this.fragmenterProperty = fragmenterProperty;
		this.fragmenterSubjectFilter = fragmenterSubjectFilter;
	}

	public Set<SubstringToken> getTokens() {
		return tokenize().stream().map(SubstringToken::new).collect(Collectors.toSet());
	}

	private List<String> tokenize() {
		final String substringTarget = getSubstringTarget();
		if (StringUtils.isBlank(substringTarget)) {
			return List.of();
		}

		final String normalizedInput = normalize(substringTarget);
		final List<String> tokens = new ArrayList<>(Arrays.asList(normalizedInput.split(" ")));
		if (tokens.size() > 1) {
			// we only add the input if it was actually split, else we end up with a list
			// with duplicate values
			tokens.add(normalizedInput);
		}
		return tokens;
	}

	private String normalize(String substringTarget) {
		return Normalizer.normalize(substringTarget.trim(), Normalizer.Form.NFKD)
				.replaceAll("\\p{M}", "")
				.replace(",", "")
				.replace("-", " ")
				.toLowerCase();
	}

	private String getSubstringTarget() {
		return (String) getFragmentationObject(fragmenterSubjectFilter, fragmenterProperty);
	}

}
