package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.serverdcat.services.ServerDcatService;
import org.apache.jena.rdf.model.Model;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/api/v1/dcat")
public class AdminServerDcatControllerImpl implements AdminServerDcatController {
	private final ServerDcatService service;

	public AdminServerDcatControllerImpl(ServerDcatService service) {
		this.service = service;
	}

	@Override
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public String postServerDcat(@RequestBody Model dcat) {
		return service.createServerDcat(dcat).getId();
	}

	@Override
	@PutMapping("/{catalogId}")
	public void putServerDcat(@PathVariable String catalogId, @RequestBody Model dcat) {
		service.updateServerDcat(catalogId, dcat);
	}

	@Override
	@DeleteMapping("/{catalogId}")
	public void deleteServerDcat(@PathVariable String catalogId) {
		service.deleteServerDcat(catalogId);
	}
}
