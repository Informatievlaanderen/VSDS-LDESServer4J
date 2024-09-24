package be.vlaanderen.informatievlaanderen.ldes.server.resultactionsextensions;

import org.apache.jena.rdf.model.Model;
import org.mockito.ArgumentMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.AssertionErrors;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_MEMBER;

public class MemberCounter implements ResultMatcher, ArgumentMatcher<Model> {
    private static final Logger log = LoggerFactory.getLogger(MemberCounter.class);
    private final int expectedMemberCount;

    private MemberCounter(int expectedMemberCount) {
        this.expectedMemberCount = expectedMemberCount;
    }

    public static MemberCounter countMembers(int count) {
        return new MemberCounter(count);
    }

    @Override
    public void match(MvcResult result) throws Exception {
        final Model model = new ResponseToModelConverter(result.getResponse()).convert();

        AssertionErrors.assertTrue("Response does not contains the expected number of members", matches(model));
    }

    @Override
    public boolean matches(Model model) {
        final int memberCount = model.listObjectsOfProperty(TREE_MEMBER).toList().size();
        if(memberCount != expectedMemberCount) {
            log.atInfo().log("Expected {} members, but received {}", expectedMemberCount, memberCount);
        }
        return expectedMemberCount == memberCount;
    }
}
