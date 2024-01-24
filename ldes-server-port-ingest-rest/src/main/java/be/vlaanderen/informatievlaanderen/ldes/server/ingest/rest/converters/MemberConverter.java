package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.exception.MemberIdNotFoundException;
import io.micrometer.observation.annotation.Observed;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("java:S1075")
@Observed
@Component
public class MemberConverter implements HttpMessageConverter<Member> {

    private static final String VERSION_OF_PATH = "http://purl.org/dc/terms/isVersionOf";

    private final RdfModelConverter rdfModelConverter;

    public MemberConverter(RdfModelConverter rdfModelConverter) {
        this.rdfModelConverter = rdfModelConverter;
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return List.of(MediaType.ALL);
    }

    @Override
    public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
        return clazz.isAssignableFrom(Member.class);
    }

    @Override
    public boolean canWrite(@NotNull Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
	public Member read(@NotNull Class<? extends Member> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		Lang lang = rdfModelConverter.getLang(Objects.requireNonNull(inputMessage.getHeaders().getContentType()),
				RdfFormatException.RdfFormatContext.INGEST);
		Model memberModel = RDFParser.source(inputMessage.getBody()).lang(lang).toModel();

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
		String collectionName = request.getRequestURI().replace(request.getContextPath() + "/", "");

		String memberId = extractMemberId(memberModel, collectionName);
		return new Member(memberId, collectionName, null, memberModel);
	}

    @Override
    public void write(@NotNull Member member, @Nullable MediaType contentType, @NotNull HttpOutputMessage outputMessage)
            throws UnsupportedOperationException, HttpMessageNotWritableException {
        throw new UnsupportedOperationException();
    }

    private String extractMemberId(Model model, String collectionName) {
        final var ids = model.listSubjectsWithProperty(ResourceFactory.createProperty(VERSION_OF_PATH)).toSet();
        if (ids.size() != 1) {
            throw new MemberIdNotFoundException(model);
        } else {
            return collectionName + "/" + ids.iterator().next().toString();
        }
    }

}
