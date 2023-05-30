package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.services.DcatServerService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.DcatCatalogValidator;
import org.apache.jena.rdf.model.Model;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/api/v1/dcat")
public class AdminServerDcatController implements OpenApiServerDcatController {
	private final DcatServerService service;
	private final DcatCatalogValidator validator;

	public AdminServerDcatController(DcatServerService service, DcatCatalogValidator validator) {
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
		return service.createDcatServer(dcat).getId();
	}

	@Override
	@PutMapping("/{catalogId}")
	public void putServerDcat(@PathVariable String catalogId, @RequestBody @Validated Model dcat) {
		service.updateDcatServer(catalogId, dcat);
	}

	@Override
	@DeleteMapping("/{catalogId}")
	public void deleteServerDcat(@PathVariable String catalogId) {
		service.deleteDcatServer(catalogId);
	}
}
