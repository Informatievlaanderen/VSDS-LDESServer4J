package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.services.ShaclShapeService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation.ModelValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation.ValidatorsConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.IsIsomorphic;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling.AdminRestResponseEntityExceptionHandler;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.HttpModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.encodig.CharsetEncodingConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.jena.riot.WebContent.contentTypeTurtle;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles({"test", "rest"})
@ContextConfiguration(classes = {AdminShapeRestController.class, HttpModelConverter.class,
        PrefixAdderImpl.class, AdminRestResponseEntityExceptionHandler.class, ValidatorsConfig.class,
        RdfModelConverter.class, CharsetEncodingConfig.class})
class AdminShapeRestControllerTest {
    @MockBean
    private ShaclShapeService shaclShapeService;

    @SpyBean(name = "shaclShapeShaclValidator")
    private ModelValidator shaclShapeValidator;

    @Autowired
    private MockMvc mockMvc;

    private String readDataFromFile(String fileName)
            throws URISyntaxException, IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
        return Files.lines(Paths.get(file.toURI())).collect(Collectors.joining("\n"));
    }

    @TestConfiguration
    static class AdminShapeRestControllerTestConfig {
        @Bean
        public HttpModelConverter modelConverter() {
            return new HttpModelConverter(new PrefixAdderImpl(List.of()), new RdfModelConverter());
        }
    }

    @Nested
    class GetRequest {
        @Test
        void when_ShapeIsPresentArePresent_Then_ShapeIsReturned() throws Exception {
            String collectionName = "name1";
            Model expectedShapeModel = RDFDataMgr.loadModel("shacl/menu-shape.ttl");
            when(shaclShapeService.retrieveShaclShape(collectionName))
                    .thenReturn(new ShaclShape(collectionName, expectedShapeModel));

            mockMvc.perform(get("/admin/api/v1/eventstreams/" + collectionName + "/shape")
                            .accept(contentTypeTurtle))
                    .andExpect(status().isOk())
                    .andExpect(content().encoding(StandardCharsets.UTF_8))
                    .andExpect(content().contentTypeCompatibleWith(contentTypeTurtle))
                    .andExpect(IsIsomorphic.with(expectedShapeModel));
        }

        @Test
        void when_ViewNotPresent_Then_Returned404() throws Exception {
            String collectionName = "name1";
            when(shaclShapeService.retrieveShaclShape(collectionName)).thenThrow(new MissingResourceException("shacl-shape", collectionName));

            mockMvc.perform(get("/admin/api/v1/eventstreams/" + collectionName + "/shape")
                            .accept(contentTypeTurtle))
                    .andExpect(status().isNotFound())
                    .andExpect(content().encoding(StandardCharsets.UTF_8))
                    .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                    .andExpect(content().string("Resource of type: shacl-shape with id: %s could not be found.".formatted(collectionName)));
        }
    }

    @Nested
    class PutRequest {
        @Test
        void when_ModelInRequestBody_Then_MethodIsCalled() throws Exception {
            String collectionName = "name1";
            String fileName = "shacl/menu-shape.ttl";
            Model expectedShapeModel = RDFDataMgr.loadModel(fileName);

            mockMvc.perform(put("/admin/api/v1/eventstreams/" + collectionName + "/shape")
                            .accept(contentTypeTurtle)
                            .content(readDataFromFile(fileName))
                            .contentType(contentTypeTurtle))
                    .andExpect(status().isOk())
                    .andExpect(content().encoding(StandardCharsets.UTF_8))
                    .andExpect(content().contentTypeCompatibleWith(contentTypeTurtle))
                    .andExpect(IsIsomorphic.with(expectedShapeModel));

            InOrder inOrder = inOrder(shaclShapeValidator, shaclShapeService);
            inOrder.verify(shaclShapeValidator, times(1)).validate(any());
            inOrder.verify(shaclShapeService, times(1))
                    .updateShaclShape(new ShaclShape(collectionName, expectedShapeModel));
            inOrder.verifyNoMoreInteractions();
        }

        @Test
        void when_ModelWithoutType_Then_ReturnedBadRequest() throws Exception {
            String collectionName = "name1";
            mockMvc.perform(put("/admin/api/v1/eventstreams/" + collectionName + "/shape")
                            .accept(contentTypeTurtle)
                            .content(readDataFromFile("shacl/shape-without-type.ttl"))
                            .contentType(contentTypeTurtle))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().encoding(StandardCharsets.UTF_8))
                    .andExpect(content().contentTypeCompatibleWith(contentTypeTurtle));
            verify(shaclShapeValidator).validate(any());
        }

    }
}
