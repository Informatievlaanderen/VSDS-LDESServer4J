package be.vlaanderen.informatievlaanderen.ldes.server.resultactionsextensions;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RDFParser;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.UnsupportedEncodingException;

public class ResponseToModelConverter {
    private final MockHttpServletResponse response;


    public ResponseToModelConverter(MockHttpServletResponse response) {
        this.response = response;
    }

    public Model convert() throws UnsupportedEncodingException {
        return RDFParser
                .fromString(response.getContentAsString())
                .lang(RDFLanguages.contentTypeToLang(response.getContentType()))
                .toModel();
    }
}

