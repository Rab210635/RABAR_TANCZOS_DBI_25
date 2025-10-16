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
import spengergasse.at.sj2425scherzerrabar.dtos.BranchDto;
import spengergasse.at.sj2425scherzerrabar.service.BranchService;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@WebMvcTest(BranchRestController.class)
@AutoConfigureRestDocs
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BranchRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BranchService branchService;

    @Test
    public void should_return_branch_when_branch_exists() throws Exception {
        BranchDto dto = new BranchDto("branch123", "library456", "123 Main St");

        when(branchService.getBranchByApiKey(any())).thenReturn(dto);

        mockMvc.perform(get("/api/branches/branch").param("apiKey","ValidApiKey")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(dto.apiKey()))
                .andExpect(jsonPath("$.libraryApiKey").value(dto.libraryApiKey()))
                .andExpect(jsonPath("$.address").value(dto.address()))
                .andDo(document("branches/get-by-api-key",
                        queryParameters(
                                parameterWithName("apiKey").description("The API key of the branch to retrieve")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("The API key of the branch"),
                                fieldWithPath("libraryApiKey").description("API key of the associated library"),
                                fieldWithPath("address").description("The address of the branch")
                        )));
    }

    @Test
    public void should_return_all_branches() throws Exception {
        BranchDto dto = new BranchDto("branch123", "library456", "123 Main St");

        when(branchService.getAllBranches()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/branches")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].apiKey").value(dto.apiKey()))
                .andExpect(jsonPath("$[0].libraryApiKey").value(dto.libraryApiKey()))
                .andExpect(jsonPath("$[0].address").value(dto.address()))
                .andDo(document("branches/get-all",
                        responseFields(
                                fieldWithPath("[].apiKey").description("The API key of the branch"),
                                fieldWithPath("[].libraryApiKey").description("API key of the associated library"),
                                fieldWithPath("[].address").description("The address of the branch")
                        )));
    }

    @Test
    public void should_return_all_branches_by_library() throws Exception {
        BranchDto dto = new BranchDto("branch123", "library456", "123 Main St");

        when(branchService.getBranchesByLibrary(any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/branches/library").param("library","ValidLibraryApiKey")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].apiKey").value(dto.apiKey()))
                .andExpect(jsonPath("$[0].libraryApiKey").value(dto.libraryApiKey()))
                .andExpect(jsonPath("$[0].address").value(dto.address()))
                .andDo(document("branches/get-all-by-library",
                        queryParameters(
                                parameterWithName("library").description("The API key of the library to retrieve All Branches")
                        ),
                        responseFields(
                                fieldWithPath("[].apiKey").description("The API key of the branch"),
                                fieldWithPath("[].libraryApiKey").description("API key of the associated library"),
                                fieldWithPath("[].address").description("The address of the branch")
                        )));
    }

    @Test
    public void should_create_branch() throws Exception {
        BranchDto dto = new BranchDto("branch123", "library456", "123 Main St");

        Link expectedSelfLink = linkTo(methodOn(BranchRestController.class).getBranch(dto.apiKey())).withSelfRel();
        String expectedLocation = expectedSelfLink.toUri().toString();
        when(branchService.createBranch(any())).thenReturn(dto);

        mockMvc.perform(post("/api/branches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"libraryApiKey\": \"library456\", "
                                + "\"address\": \"123 Main St\""
                                + "}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(dto.apiKey()))
                .andExpect(header().string("Location", containsString(expectedLocation)))
                .andDo(document("branches/post-create-branch",
                        requestFields(
                                fieldWithPath("libraryApiKey").description("API key of the associated library"),
                                fieldWithPath("address").description("The address of the branch")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("Generated API key for the branch"),
                                fieldWithPath("libraryApiKey").description("API key of the associated library"),
                                fieldWithPath("address").description("The address of the branch")
                        )));
    }

    @Test
    public void should_update_branch() throws Exception {
        BranchDto dto = new BranchDto("branch123", "library456", "123 Updated St");

        when(branchService.updateBranch(any())).thenReturn(dto);

        mockMvc.perform(put("/api/branches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"apiKey\": \"branch123\", "
                                + "\"libraryApiKey\": \"library456\", "
                                + "\"address\": \"123 Updated St\""
                                + "}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(dto.apiKey()))
                .andExpect(jsonPath("$.libraryApiKey").value(dto.libraryApiKey()))
                .andExpect(jsonPath("$.address").value(dto.address()))
                .andDo(document("branches/put-update-branch",
                        requestFields(
                                fieldWithPath("apiKey").description("The API key of the branch"),
                                fieldWithPath("libraryApiKey").description("API key of the associated library"),
                                fieldWithPath("address").description("The updated address of the branch")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("The API key of the branch"),
                                fieldWithPath("libraryApiKey").description("API key of the associated library"),
                                fieldWithPath("address").description("The updated address of the branch")
                        )));
    }

    @Test
    public void should_delete_branch() throws Exception {
        mockMvc.perform(delete("/api/branches?apiKey=ValidApiKeyToDelete"))
                .andExpect(status().isNoContent())
                .andDo(document("branches/delete-delete-branch",
                        queryParameters(
                                parameterWithName("apiKey").description("The API key of the branch to delete")
                        )));
    }

    @Test
    public void should_respond_with_correct_exceptions() throws Exception {
        when(branchService.getBranchByApiKey(any())).thenThrow(BranchService.BranchServiceException.noBranchForApiKey("InvalidApiKey"));

        mockMvc.perform(get("/api/branches/branch").param("apiKey", "InvalidApiKey")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Branch Service Error"))
                .andExpect(jsonPath("$.detail").value("Branch with api key (InvalidApiKey) not existent"))
                .andDo(document("branches/get-not-found",
                        queryParameters(
                                parameterWithName("apiKey").description("The API key of the branch to retrieve")
                        ),
                        responseFields(
                                fieldWithPath("title").description("Error title"),
                                fieldWithPath("status").description("HTTP status code"),
                                fieldWithPath("detail").description("Error detail"),
                                fieldWithPath("type").description("Error type"),
                                fieldWithPath("instance").description("The request URL")
                        )));
    }
}
