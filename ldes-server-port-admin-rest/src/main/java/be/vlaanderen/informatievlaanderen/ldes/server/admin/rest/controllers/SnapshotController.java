package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services.SnapshotService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/api/v1")
public class SnapshotController {

	private final SnapshotService snapshotService;

	private final AppConfig appConfig;

	public SnapshotController(SnapshotService snapshotService, AppConfig appConfig) {
		this.snapshotService = snapshotService;
		this.appConfig = appConfig;
	}

	@PostMapping("{collection}/snapshots")
	public void createSnapshot(@PathVariable("collection") String collectionName) {
		LdesConfig ldesConfig = appConfig.getLdesConfig(collectionName);
		snapshotService.createSnapshot(ldesConfig);
	}
}
