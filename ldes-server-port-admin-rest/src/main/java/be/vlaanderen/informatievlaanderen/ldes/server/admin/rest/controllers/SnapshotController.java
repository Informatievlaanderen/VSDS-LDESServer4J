package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services.SnapshotService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/api/v1")
@Tag(name = "Snapshots")
public class SnapshotController {

	private final SnapshotService snapshotService;

	public SnapshotController(SnapshotService snapshotService) {
		this.snapshotService = snapshotService;
	}

	@PostMapping("{collection}/snapshots")
	@Operation(summary = "Creation of Snapshot")
	public void createSnapshot(@PathVariable("collection") String collectionName) {
		snapshotService.createSnapshot(collectionName);
	}
}
