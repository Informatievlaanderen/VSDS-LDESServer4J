package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;

import static org.apache.jena.riot.WebContent.contentTypeJSONLD;
import static org.apache.jena.riot.WebContent.contentTypeNQuads;
import static org.apache.jena.riot.WebContent.contentTypeNTriples;
import static org.apache.jena.riot.WebContent.contentTypeRDFProto;
import static org.apache.jena.riot.WebContent.contentTypeRDFXML;
import static org.apache.jena.riot.WebContent.contentTypeTurtle;

public enum RdfMediaType {
    TEXT_TURTLE(MediaType.valueOf(contentTypeTurtle)),
    APPLICATION_RDF_XML(MediaType.valueOf(contentTypeRDFXML)),
    APPLICATION_N_TRIPLES(MediaType.valueOf(contentTypeNTriples)),
    APPLICATION_N_QUADS(MediaType.valueOf(contentTypeNQuads)),
    APPLICATION_JSON_LD(MediaType.valueOf(contentTypeJSONLD)),
    TEXT_HTML(MediaType.TEXT_HTML),
    TEXT_EVENT_STREAM(MediaType.TEXT_EVENT_STREAM),
    APPLICATION_PROTOBUF(MediaType.valueOf(contentTypeRDFProto));

    public static final String DEFAULT_RDF_MEDIA_TYPE_VALUE = contentTypeTurtle;
    public static final MediaType DEFAULT_RDF_MEDIA_TYPE = MediaType.valueOf(DEFAULT_RDF_MEDIA_TYPE_VALUE);

    private final MediaType mediaType;

    RdfMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public static List<MediaType> getMediaTypes() {
        return Arrays.stream(RdfMediaType.values()).map(RdfMediaType::getMediaType).toList();
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public String getContentType() {
        return mediaType.getType() + "/" + mediaType.getSubtype();
    }
}
