package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation.ModelValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.service.ViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.ViewSpecificationConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import io.micrometer.observation.annotation.Observed;
import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.apache.jena.riot.WebContent.*;

@Observed
@RestController
@RequestMapping("/admin/api/v1")
public class AdminViewsRestController implements OpenApiAdminViewsRestController {
    private final ViewService viewService;
    private final ModelValidator viewValidator;
    private final ViewSpecificationConverter viewConverter;

    public AdminViewsRestController(ViewService viewService,
                                    @Qualifier("viewShaclValidator") ModelValidator viewValidator,
                                    ViewSpecificationConverter viewConverter) {
        this.viewService = viewService;
        this.viewValidator = viewValidator;
        this.viewConverter = viewConverter;
    }

    @InitBinder
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(viewValidator);
    }

    @GetMapping(value = "/eventstreams/{collectionName}/views", produces = {contentTypeTurtle, contentTypeJSONLD,
            contentTypeNQuads})
    public List<ViewSpecification> getViews(@PathVariable String collectionName) {
        return viewService.getViewsByCollectionName(collectionName);
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(value = "/eventstreams/{collectionName}/views", consumes = {contentTypeTurtle, contentTypeJSONLD,
            contentTypeNQuads})
    public void createView(@PathVariable String collectionName,
                           @RequestBody @Validated Model view) {
        viewValidator.validate(view);
        viewService.addView(viewConverter.viewFromModel(view, collectionName));
    }

    @DeleteMapping("/eventstreams/{collectionName}/views/{viewName}")
    public void deleteView(@PathVariable String collectionName, @PathVariable String viewName) {
        viewService.deleteViewByViewName(new ViewName(collectionName, viewName));
    }

    @GetMapping(value = "/eventstreams/{collectionName}/views/{viewName}", produces = {contentTypeTurtle,
            contentTypeJSONLD,
            contentTypeNQuads})
    public ViewSpecification getViewOfCollection(@PathVariable String collectionName,
                                                 @PathVariable String viewName) {
        return viewService.getViewByViewName(new ViewName(collectionName, viewName));
    }
}
