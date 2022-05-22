package be.vlaanderen.informatievlaanderen.ldes.server.domain.converters;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.vocabulary.VCARD;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JenaConverterImplTest {
    private final JenaConverter jenaConverter = new JenaConverterImpl();
    private Model model;

    private final static String PERSON_URI = "http://somewhere/JohnSmith";
    private final static String FULL_NAME = "John Smith";
    private final static Property VCARD_FN_PROPERTY = VCARD.FN;

    @BeforeEach
    void init() {
        model = ModelFactory.createDefaultModel();
        model.createResource(PERSON_URI).addProperty(VCARD_FN_PROPERTY, FULL_NAME);
    }

    @ParameterizedTest(name = "Correct serialization using format {0}")
    @ArgumentsSource(RdfFormatSerializationArgsProvider.class)
    @DisplayName("Serialization of RDF model")
    void when_ModelIsProvided_ConverterReturnsStringInRDFFormat(RDFFormat rdfFormat, String expectedSerialization) {
        String s = jenaConverter.writeModelToString(model, rdfFormat);
        assertEquals(expectedSerialization, s);
    }

    @ParameterizedTest(name = "Correct deserialization using format {0}")
    @ArgumentsSource(LangSerializationArgsProvider.class)
    @DisplayName("Deserialization of RDF model")
    void when_StringAndLangAreProvided_ConverterReturnsModel(Lang lang, String serialization) {
        Model model = ModelFactory.createDefaultModel();
        jenaConverter.readModelFromString(serialization, model, lang);
        Resource resource = model.getResource(PERSON_URI);
        assertEquals(FULL_NAME, resource.getProperty(VCARD_FN_PROPERTY).getString());
    }

    static class RdfFormatSerializationArgsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(RDFFormat.JSONLD11, getJsonLdSerialization()),
                    Arguments.of(RDFFormat.NQUADS, getNquadsSerialization()));
        }
    }

    static class LangSerializationArgsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(Lang.JSONLD11, getJsonLdSerialization()),
                    Arguments.of(Lang.NQUADS, getNquadsSerialization()));
        }
    }

    private static String getNquadsSerialization() {
        return "<http://somewhere/JohnSmith> <http://www.w3.org/2001/vcard-rdf/3.0#FN> \"John Smith\" .\n";
    }

    private static String getJsonLdSerialization() {
        return "{\n" + "    \"@context\": {\n" + "        \"@version\": 1.1\n" + "    },\n" + "    \"@graph\": [\n"
                + "        {\n" + "            \"@id\": \"http://somewhere/JohnSmith\",\n"
                + "            \"http://www.w3.org/2001/vcard-rdf/3.0#FN\": [\n" + "                {\n"
                + "                    \"@value\": \"John Smith\"\n" + "                }\n" + "            ]\n"
                + "        }\n" + "    ]\n" + "}\n";
    }

}