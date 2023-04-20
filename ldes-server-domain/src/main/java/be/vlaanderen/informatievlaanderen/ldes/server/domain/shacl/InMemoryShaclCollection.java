package be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.services.LdesConfigModelService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import jakarta.annotation.PostConstruct;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryShaclCollection implements ShaclCollection {
	private final Map<String, ShaclShape> shapes;
	private final LdesConfigModelService ldesConfigModelService;

	public InMemoryShaclCollection(LdesConfigModelService ldesConfigModelService) {
		this.shapes = new HashMap<>();
		this.ldesConfigModelService = ldesConfigModelService;
	}

	@EventListener
	public void handleShaclChangedEvent(ShaclChangedEvent event) {
		shapes.put(event.getCollectionName(), event.getShacl());
		ldesConfigModelService.updateShape(event.getCollectionName(),
				new LdesConfigModel(event.getCollectionName(), event.getShacl()));
	}

	@Override
	public ShaclShape retrieveShape(String collectionName) {
		return shapes.get(collectionName);
	}

	@PostConstruct
	private void initShapes() {
		ldesConfigModelService
				.retrieveAllShapes()
				.forEach(ldesConfigModel -> shapes.put(ldesConfigModel.getId(), ldesConfigModel.getModel()));
	}
}
