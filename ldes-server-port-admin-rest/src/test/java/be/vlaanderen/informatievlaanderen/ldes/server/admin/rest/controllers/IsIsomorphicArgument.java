package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import org.apache.jena.rdf.model.Model;
import org.mockito.ArgumentMatcher;

public class IsIsomorphicArgument implements ArgumentMatcher<Model> {
	private final Model model;

	IsIsomorphicArgument(Model model) {
		this.model = model;
	}

	public static IsIsomorphicArgument with(Model model) {
		return new IsIsomorphicArgument(model);
	}

	@Override
	public boolean matches(Model actualModel) {
		return actualModel.isIsomorphicWith(model);
	}
}
