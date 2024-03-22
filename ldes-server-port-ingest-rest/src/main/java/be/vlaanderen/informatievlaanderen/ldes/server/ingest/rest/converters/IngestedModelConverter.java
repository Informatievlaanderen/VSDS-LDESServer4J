package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.exception.IngestValidationException;
import io.micrometer.observation.annotation.Observed;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
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

import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Observed
@Component
public class IngestedModelConverter implements HttpMessageConverter<Model> {

	private final RdfModelConverter rdfModelConverter;

	public IngestedModelConverter(RdfModelConverter rdfModelConverter) {
		this.rdfModelConverter = rdfModelConverter;
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return List.of(MediaType.ALL);
	}

	@Override
	public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
		return clazz.isAssignableFrom(Model.class);
	}

	@Override
	public boolean canWrite(@NotNull Class<?> clazz, MediaType mediaType) {
		return false;
	}

	@Override
	public Model read(@NotNull Class<? extends Model> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		final Lang lang =
				rdfModelConverter.getLang(
						requireNonNull(inputMessage.getHeaders().getContentType()),
						RdfFormatException.RdfFormatContext.INGEST
				);
		Dataset dataset = RDFParser.source(inputMessage.getBody()).context(rdfModelConverter.getContext()).lang(lang).toDataset();
		if (dataset.listModelNames().hasNext()) {
			throw new IngestValidationException("Member can not contain named graphs");
		}
		return dataset.getDefaultModel();
	}

	@Override
	public void write(@NotNull Model model, @Nullable MediaType contentType, @NotNull HttpOutputMessage outputMessage)
			throws UnsupportedOperationException, HttpMessageNotWritableException {
		throw new UnsupportedOperationException();
	}
}
