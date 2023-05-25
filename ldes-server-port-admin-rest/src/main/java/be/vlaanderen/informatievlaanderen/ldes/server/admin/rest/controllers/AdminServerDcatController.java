package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.serverdcat.services.ServerDcatService;
import org.apache.jena.rdf.model.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/api/v1/dcat")
public class AdminServerDcatController {
	private final ServerDcatService service;

	public AdminServerDcatController(ServerDcatService service) {
		this.service = service;
	}

	@PostMapping
	public String postServerDcat(@RequestBody Model dcat) {
		return service.createServerDcat(dcat).getId();
	}

	@PutMapping("/{catalogId}")
	public String putServerDcat(@PathVariable String catalogId, @RequestBody Model dcat) {
		return service.updateServerDcat(catalogId, dcat).getId();
	}

	@DeleteMapping("/{catalogId}")
	public void deleteServerDcat(@PathVariable String catalogId) {
		service.deleteServerDcat(catalogId);
	}
}
