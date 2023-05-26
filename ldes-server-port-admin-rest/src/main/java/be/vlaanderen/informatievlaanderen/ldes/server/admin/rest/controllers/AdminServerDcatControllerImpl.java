package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.serverdcat.services.ServerDcatService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.DcatCatalogValidator;
import org.apache.jena.rdf.model.Model;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/api/v1/dcat")
public class AdminServerDcatControllerImpl implements AdminServerDcatController {
	private final ServerDcatService service;
	private final DcatCatalogValidator validator;

	public AdminServerDcatControllerImpl(ServerDcatService service, DcatCatalogValidator validator) {
		this.service = service;
		this.validator = validator;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}

	@Override
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public String postServerDcat(@RequestBody @Validated Model dcat) {
		return service.createServerDcat(dcat).getId();
	}

	@Override
	@PutMapping("/{catalogId}")
	public void putServerDcat(@PathVariable String catalogId, @RequestBody @Validated Model dcat) {
		service.updateServerDcat(catalogId, dcat);
	}

	@Override
	@DeleteMapping("/{catalogId}")
	public void deleteServerDcat(@PathVariable String catalogId) {
		service.deleteServerDcat(catalogId);
	}
}
