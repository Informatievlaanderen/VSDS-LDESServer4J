package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.dtos.LdesConfigModelListDto;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;
import org.apache.jena.riot.RDFDataMgr;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.List;

import static org.apache.jena.riot.RDFFormat.TURTLE;

public class LdesConfigModelListConverter extends AbstractHttpMessageConverter<LdesConfigModelListDto> {

	@Override
	protected boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(LdesConfigModelListDto.class);
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return List.of(MediaType.ALL);
	}

	@Override
	protected LdesConfigModelListDto readInternal(Class<? extends LdesConfigModelListDto> clazz,
			HttpInputMessage inputMessage) throws HttpMessageNotReadableException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void writeInternal(LdesConfigModelListDto ldesConfigModelListDto, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		StringWriter outputStream = new StringWriter();

		ldesConfigModelListDto.getLdesConfigModelList().stream()
				.map(LdesConfigModel::getModel)
				.forEach(model -> RDFDataMgr.write(outputStream, model, TURTLE));

		OutputStream body = outputMessage.getBody();
		body.write(outputStream.toString().getBytes());
	}
}
