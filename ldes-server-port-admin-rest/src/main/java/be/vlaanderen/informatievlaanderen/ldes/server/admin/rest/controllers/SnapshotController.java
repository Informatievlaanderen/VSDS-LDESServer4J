package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services.SnapshotService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/api/v1")
public class SnapshotController {

	private final SnapshotService snapshotService;

	public SnapshotController(SnapshotService snapshotService) {
		this.snapshotService = snapshotService;
	}

	@PostMapping("/snapshots")
	public void createSnapshot() {
		snapshotService.createSnapshot();
	}
}
