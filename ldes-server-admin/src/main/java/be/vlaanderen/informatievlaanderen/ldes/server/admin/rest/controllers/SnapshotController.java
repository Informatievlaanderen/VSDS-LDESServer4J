package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

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

	@PostMapping("{collection}/snapshots")
	@Operation(summary = "Creation of Snapshot")
	public void createSnapshot(@PathVariable("collection") String collectionName) {
		// replace with firing event if snapshot is ever supported again
		// snapshotService.createSnapshot(collectionName);
	}
}
