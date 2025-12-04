package be.vlaanderen.informatievlaanderen.ldes.server.resultactionsextensions;

import org.apache.jena.atlas.web.ContentType;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RDFParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.UnsupportedEncodingException;

public class ResponseToModelConverter {
    private static final Logger log = LoggerFactory.getLogger(ResponseToModelConverter.class);
    private final MockHttpServletResponse response;


    public ResponseToModelConverter(MockHttpServletResponse response) {
        this.response = response;
    }

    public Model convert() throws UnsupportedEncodingException {
        String contentAsString = response.getContentAsString();
        log.atTrace().log("Response content: {}", contentAsString);
        Lang lang = RDFLanguages.contentTypeToLang(ContentType.create(response.getContentType()));
        return RDFParser.create().fromString(contentAsString).lang(lang).toModel();
    }
}

