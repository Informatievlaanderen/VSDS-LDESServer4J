package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.services.DcatServerService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation.dcat.DcatCatalogValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.ShaclValidationException;
import io.micrometer.observation.annotation.Observed;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import static org.apache.jena.riot.WebContent.contentTypeTurtle;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Observed
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
	@GetMapping
	public ResponseEntity<Model> getDcat(@RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = contentTypeTurtle) String language,
			HttpServletResponse response) {
		setContentTypeHeader(language, response);
		try {
			return ResponseEntity.ok(service.getComposedDcat());
		} catch (ShaclValidationException e) {
			return ResponseEntity.internalServerError().body(e.getValidationReportModel());
		}
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

	private void setContentTypeHeader(String language, HttpServletResponse response) {
		if (language.equals(MediaType.ALL_VALUE) || language.contains(MediaType.TEXT_HTML_VALUE)) {
			response.setHeader(CONTENT_TYPE, Lang.TURTLE.getHeaderString());
		} else {
			response.setHeader(CONTENT_TYPE, language.split(",")[0]);
		}
	}

}
