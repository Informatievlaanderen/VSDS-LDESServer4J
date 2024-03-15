package be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CDBExtractorTest {

    @Test
    void test_ExtractNamedSubjects() {
        final List<Resource> expectedNamedNodes = Stream.of("bart", "lisa", "homer")
                .map("http://temporary.org#%s"::formatted)
                .map(ResourceFactory::createResource)
                .toList();
        final Model model = RDFParser.source("bulk-members/simpsons/all.nq").lang(Lang.NQ).toModel();

        final List<RDFNode> namedNodes = CBDExtractor.initialize(model).getNamedSubjects();

        assertThat(namedNodes).containsExactlyInAnyOrderElementsOf(expectedNamedNodes);
    }

    @ParameterizedTest
    @ArgumentsSource(SingleMemberExtractionArgumentsProvider.class)
    void test_extractModel(String expectedModelFileName, String modelToExtractFileName, String subjectUri) {
        final Model expectedModel = RDFParser.source(expectedModelFileName).lang(Lang.NQ).toModel();
        final Model modelToExtract = RDFParser.source(modelToExtractFileName).lang(Lang.NQ).toModel();
        final Resource subjectToExtract = ResourceFactory.createResource(subjectUri);

        final Model actualModel = CBDExtractor.initialize(modelToExtract).extractMemberModel(subjectToExtract);

        assertThat(actualModel).matches(expectedModel::isIsomorphicWith);
    }

    @ParameterizedTest
    @ArgumentsSource(AllMembersExtractionArgumentsProvider.class)
    void test_extractAllMembersFromModel(String modelToExtractFileName, List<String> memberFileNames) {
        final List<Model> expectedMembers = memberFileNames.stream()
                .map(fileName -> RDFParser.source(fileName).lang(Lang.NQ).toModel())
                .toList();
        final Model modelToExtract = RDFParser.source(modelToExtractFileName).lang(Lang.NQ).toModel();

        final List<Model> actualMembers = CBDExtractor.initialize(modelToExtract).extractAllMemberModels();

        assertThat(actualMembers)
                .isNotEmpty()
                .allMatch(actualMember -> expectedMembers.stream().anyMatch(actualMember::isIsomorphicWith));
    }

    static class SingleMemberExtractionArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
            return Stream.of(
                    Arguments.of("bulk-members/simpsons/bart.nq", "bulk-members/simpsons/all.nq", "http://temporary.org#bart"),
                    Arguments.of("bulk-members/simpsons/homer.nq", "bulk-members/simpsons/all.nq", "http://temporary.org#homer"),
                    Arguments.of("bulk-members/people/a-person.nq", "bulk-members/people/multiple.nq", "http://example.org/A"),
                    Arguments.of("bulk-members/single-member-with-nested-bnodes.nq", "bulk-members/single-member-with-nested-bnodes.nq", "http://example.org/A")
            );
        }
    }

    static class AllMembersExtractionArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
            return Stream.of(
                    Arguments.of("bulk-members/simpsons/all.nq", List.of("bulk-members/simpsons/bart.nq", "bulk-members/simpsons/homer.nq", "bulk-members/simpsons/lisa.nq")),
                    Arguments.of("bulk-members/people/multiple.nq", List.of("bulk-members/people/a-person.nq", "bulk-members/people/b-person.nq")),
                    Arguments.of("bulk-members/single-member-with-nested-bnodes.nq", List.of("bulk-members/single-member-with-nested-bnodes.nq"))
            );
        }
    }
}