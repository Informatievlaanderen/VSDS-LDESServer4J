package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RelativeUrlException;
import com.apicatalog.jsonld.JsonLdOptions;
import com.apicatalog.jsonld.context.cache.LruCache;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RIOT;
import org.apache.jena.sparql.util.Context;
import org.apache.jena.sparql.util.ContextAccumulator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.StringWriter;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.MAX_JSONLD_CACHE_CAPACITY;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.USE_RELATIVE_URL_KEY;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.RELATIVE_URL_INCOMPATIBLE_LANGS;
import static java.util.Optional.ofNullable;
import static org.apache.jena.riot.RDFLanguages.TURTLE;
import static org.apache.jena.riot.RDFLanguages.nameToLang;
import static org.apache.jena.riot.lang.LangJSONLD11.JSONLD_OPTIONS;

@Component
public class RdfModelConverter {

    @Value(USE_RELATIVE_URL_KEY)
    private boolean useRelativeUrl;

    @Value(MAX_JSONLD_CACHE_CAPACITY)
    private int maxJsonLdCacheCapacity;

    private Context context;

    public Lang getLangOrDefault(MediaType contentType, RdfFormatException.RdfFormatContext rdfFormatContext) {
        if (MediaType.ALL.equals(contentType) || MediaType.TEXT_HTML.equals(contentType)) {
            return TURTLE;
        }
        return ofNullable(nameToLang(contentType.getType() + "/" + contentType.getSubtype()))
                .orElseGet(() -> ofNullable(nameToLang(contentType.getSubtype()))
                        .orElseThrow(() -> new RdfFormatException(contentType.toString(), rdfFormatContext)));
    }

    public void checkLangForRelativeUrl(Lang lang) {
        if (useRelativeUrl && RELATIVE_URL_INCOMPATIBLE_LANGS.contains(lang)) {
            throw new RelativeUrlException(lang);
        }
    }

    public static String toString(final Model model, final Lang lang) {
        StringWriter stringWriter = new StringWriter();
        RDFDataMgr.write(stringWriter, model, lang);
        return stringWriter.toString();
    }

    public synchronized Context getContext() {
        if (context == null) {
            context = createContext();
        }
        return context;
    }

    private Context createContext() {
        final var options = new JsonLdOptions();
        options.setDocumentCache(new LruCache<>(maxJsonLdCacheCapacity));
        return ContextAccumulator.newBuilder(RIOT::getContext).context().set(JSONLD_OPTIONS, options);
    }

}
