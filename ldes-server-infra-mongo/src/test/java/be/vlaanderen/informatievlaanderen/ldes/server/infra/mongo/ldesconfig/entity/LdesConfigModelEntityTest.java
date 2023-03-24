package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.ldesconfig.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.ldesconfig.entity.LdesConfigModelEntity.fromLdesConfigtModel;
import static org.junit.Assert.assertTrue;

class LdesConfigModelEntityTest {

	@Test
	void when_LdesConfigModelConvertedAndBack_Then_ModelIsIsomorphic() throws URISyntaxException {
		String fileName = "ldes-multiple-views.ttl";
		Model model = readModelFromFile(fileName);
		LdesConfigModel configModel = new LdesConfigModel("stream", model);
		LdesConfigModelEntity entity = fromLdesConfigtModel(configModel);
		LdesConfigModel actualConfigModel = entity.toLdesConfigModel();
		Assertions.assertTrue(actualConfigModel.getModel().isIsomorphicWith(model));
	}

	private String readDataFromFile(String fileName, Lang rdfFormat)
			throws URISyntaxException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
		String content = Files.lines(Paths.get(file.toURI())).collect(Collectors.joining("\n"));
		return content;
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI()
				.toString();
		return RDFDataMgr.loadModel(uri);
	}
}
