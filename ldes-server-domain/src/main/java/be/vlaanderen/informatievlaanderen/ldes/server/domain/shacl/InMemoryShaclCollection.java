package be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.services.LdesConfigModelService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;
import jakarta.annotation.PostConstruct;
import org.apache.jena.rdf.model.Model;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryShaclCollection implements ShaclCollection {
	private final Map<String, Model> shapes;
	private final LdesConfigModelService ldesConfigModelService;

	public InMemoryShaclCollection(LdesConfigModelService ldesConfigModelService) {
		this.shapes = new HashMap<>();
		this.ldesConfigModelService = ldesConfigModelService;
	}

	@PostConstruct
	private void initShapeConfig() {
		ldesConfigModelService
				.retrieveAllShapes()
				.forEach(ldesConfigModel -> shapes.put(ldesConfigModel.getId(), ldesConfigModel.getModel()));
	}

	@EventListener
	public void handleShaclChangedEvent(ShaclChangedEvent event) {
		shapes.put(event.getCollectionName(), event.getShacl());
		ldesConfigModelService.updateShape(event.getCollectionName(),
				new LdesConfigModel(event.getCollectionName(), event.getShacl()));
	}

	@Override
	public LdesConfigModel retrieveShape(String collectionName) {
		return new LdesConfigModel(collectionName, shapes.get(collectionName));
	}
}
