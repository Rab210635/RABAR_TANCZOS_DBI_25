package spengergasse.at.sj2425scherzerrabar.presentation.RestController;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import spengergasse.at.sj2425scherzerrabar.FixturesFactory;
import spengergasse.at.sj2425scherzerrabar.domain.Publisher;
import spengergasse.at.sj2425scherzerrabar.dtos.PublisherDto;
import spengergasse.at.sj2425scherzerrabar.service.PublisherService;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@WebMvcTest(PublisherRestController.class)
@AutoConfigureRestDocs
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PublisherRestControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private PublisherService publisherService;

    @Test
    public void return_publisher_when_existent() throws Exception {
        Publisher publisher = FixturesFactory.publisher(FixturesFactory.address2());
        PublisherDto publisherDto = PublisherDto.publisherDtoFromPublisher(publisher);

        when(publisherService.getPublisherByApiKey(any())).thenReturn(publisherDto);

        mvc.perform(get("/api/publishers/publisher").param("apiKey","ValidApiKey")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(publisherDto.apiKey()))
                .andExpect(jsonPath("$.name").value(publisherDto.name()))
                .andExpect(jsonPath("$.address").value(publisherDto.address()))
                .andDo(document("publishers/get-by-api-key",
                        queryParameters(
                            parameterWithName("apiKey").description("The API key of the publisher to retrieve")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("The API key of the publisher"),
                                fieldWithPath("name").description("The name of the publisher"),
                                fieldWithPath("address").description("The address of the publisher")
                        )));
    }

    @Test
    public void return_publisher_by_name_when_existent() throws Exception {
        Publisher publisher = FixturesFactory.publisher(FixturesFactory.address2());
        PublisherDto publisherDto = PublisherDto.publisherDtoFromPublisher(publisher);

        when(publisherService.getPublisherByName(any())).thenReturn(publisherDto);

        mvc.perform(get("/api/publishers/publisher").param("name","ValidPublisherName")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(publisherDto.apiKey()))
                .andExpect(jsonPath("$.name").value(publisherDto.name()))
                .andExpect(jsonPath("$.address").value(publisherDto.address()))
                .andDo(document("publishers/get-by-name",
                        queryParameters(
                                parameterWithName("name").description("The name of the publisher to retrieve")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("The API key of the publisher"),
                                fieldWithPath("name").description("The name of the publisher"),
                                fieldWithPath("address").description("The address of the publisher")
                        )));
    }

    @Test
    public void return_all_publishers() throws Exception {
        Publisher publisher = FixturesFactory.publisher(FixturesFactory.address2());
        PublisherDto publisherDto = PublisherDto.publisherDtoFromPublisher(publisher);

        when(publisherService.getAllPublishers()).thenReturn(List.of(publisherDto));

        mvc.perform(get("/api/publishers")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].apiKey").value(publisherDto.apiKey()))
                .andExpect(jsonPath("$[0].name").value(publisherDto.name()))
                .andExpect(jsonPath("$[0].address").value(publisherDto.address()))
                .andDo(document("publishers/get-all",
                        responseFields(
                                fieldWithPath("[].apiKey").description("The API key of the publisher"),
                                fieldWithPath("[].name").description("The name of the publisher"),
                                fieldWithPath("[].address").description("The address of the publisher")
                        )));
    }

    @Test
    public void create_publisher() throws Exception {
        Publisher publisher = FixturesFactory.publisher(FixturesFactory.address2());
        PublisherDto publisherDto = PublisherDto.publisherDtoFromPublisher(publisher);

        Link expectedSelfLink = linkTo(methodOn(PublisherRestController.class).getPublisher(Optional.of(publisherDto.apiKey()),null)).withSelfRel();
        String expectedLocation = expectedSelfLink.toUri().toString();

        when(publisherService.createPublisher(any())).thenReturn(publisherDto);

        mvc.perform(post("/api/publishers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"name\": \"Dornbund\", "
                                + "\"address\": \"Reumanplatz 66-Vienna-11001\""
                                + "}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(publisherDto.apiKey()))
                .andExpect(jsonPath("$.name").value(publisherDto.name()))
                .andExpect(jsonPath("$.address").value(publisherDto.address()))
                .andExpect(header().string("Location", containsString(expectedLocation)))
                .andDo(document("publishers/post-create-publisher",
                        requestFields(
                                fieldWithPath("name").description("The name of the publisher to retrieve"),
                                fieldWithPath("address").description("The name of the publisher to retrieve")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("The API key of the publisher"),
                                fieldWithPath("name").description("The name of the publisher"),
                                fieldWithPath("address").description("The address of the publisher")
                        )));
    }

    @Test
    public void update_publisher() throws Exception {
        Publisher publisher = FixturesFactory.publisher(FixturesFactory.address2());
        PublisherDto publisherDto = PublisherDto.publisherDtoFromPublisher(publisher);

        when(publisherService.updatePublisher(any())).thenReturn(publisherDto);

        mvc.perform(put("/api/publishers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"name\": \"Dornbund\", "
                                + "\"address\": \"Reumanplatz 66-Vienna-11001\""
                                + "}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(publisherDto.apiKey()))
                .andExpect(jsonPath("$.name").value(publisherDto.name()))
                .andExpect(jsonPath("$.address").value(publisherDto.address()))
                .andDo(document("publishers/put-update-publisher",
                        requestFields(
                                fieldWithPath("name").description("The name of the publisher to retrieve"),
                                fieldWithPath("address").description("The name of the publisher to retrieve")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("The API key of the publisher"),
                                fieldWithPath("name").description("The name of the publisher"),
                                fieldWithPath("address").description("The address of the publisher")
                        )));
    }

    @Test
    public void delete_publisher() throws Exception {
        Publisher publisher = FixturesFactory.publisher(FixturesFactory.address2());
        PublisherDto publisherDto = PublisherDto.publisherDtoFromPublisher(publisher);

        mvc.perform(delete("/api/publishers?apiKey=ValidApiKeyToDelete"))
                .andExpect(status().isNoContent())
                .andDo(document("publishers/delete-delete-publisher",
                        queryParameters(
                                parameterWithName("apiKey").description("The API key of the publisher to delete")
                        )));
    }

    @Test
    public void respond_with_correct_exceptions() throws Exception {
        when(publisherService.getPublisherByApiKey(any())).thenThrow(PublisherService.PublisherServiceException.noPublisherForApiKey("non-existent-api-key"));

        mvc.perform(get("/api/publishers/publisher").param("apiKey","InvalidApiKey")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Publisher Service Error"))
                .andExpect(jsonPath("$.detail").value("Publisher with api key (non-existent-api-key) not existent"))
                .andDo(document("publishers/get-not-found",
                        queryParameters(
                                parameterWithName("apiKey").description("The API key of the book to retrieve")
                        ),
                        responseFields(
                                fieldWithPath("title").description("Error title"),
                                fieldWithPath("status").description("HTTP status code"),
                                fieldWithPath("detail").description("Error detail"),
                                fieldWithPath("type").description("THE TYPE"),
                                fieldWithPath("instance").description("the url used for the request")
                        )));
    }

    @Test
    public void should_respond_with_correct_exceptions_for_no_param_in_get() throws Exception {

        mvc.perform(get("/api/publishers/publisher")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Bad Request, Parameter for get Publisher needed"))
                .andDo(document("publishers/get-with-invalid-params",
                        responseFields(
                                fieldWithPath("title").description("Error title"),
                                fieldWithPath("status").description("HTTP status code"),
                                fieldWithPath("detail").description("Error detail"),
                                fieldWithPath("type").description("THE TYPE"),
                                fieldWithPath("instance").description("the url used for the request")
                        )));
    }
}