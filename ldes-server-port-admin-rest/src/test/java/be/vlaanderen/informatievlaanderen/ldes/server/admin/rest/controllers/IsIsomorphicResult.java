package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.springframework.test.util.AssertionErrors;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

public class IsIsomorphicResult implements ResultMatcher {
	private final Model model;

	IsIsomorphicResult(Model model) {
		this.model = model;
	}

	public static IsIsomorphicResult with(Model model) {
		return new IsIsomorphicResult(model);
	}

	@Override
	public void match(MvcResult result) throws Exception {
		Model actualModel = RdfModelConverter.fromString(result.getResponse().getContentAsString(), Lang.TURTLE);
		AssertionErrors.assertTrue("Result should be isomorphic with provided model",
				actualModel.isIsomorphicWith(model));
	}
}
