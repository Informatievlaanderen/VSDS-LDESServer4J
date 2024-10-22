package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.mockito.ArgumentMatcher;
import org.springframework.test.util.AssertionErrors;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

public class IsIsomorphic implements ResultMatcher, ArgumentMatcher<Model> {
	private final Model model;

	private IsIsomorphic(Model model) {
		this.model = model;
	}

	public static IsIsomorphic with(Model model) {
		return new IsIsomorphic(model);
	}

	@Override
	public void match(MvcResult result) throws Exception {
		Model actualModel = RDFParser.fromString(result.getResponse().getContentAsString()).lang(Lang.TURTLE).toModel();
		AssertionErrors.assertTrue("Result should be isomorphic with provided model",
				actualModel.isIsomorphicWith(model));
	}

	@Override
	public boolean matches(Model actualModel) {
		return actualModel.isIsomorphicWith(model);
	}

}
