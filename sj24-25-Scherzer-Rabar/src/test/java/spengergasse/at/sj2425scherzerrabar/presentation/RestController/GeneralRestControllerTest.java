package spengergasse.at.sj2425scherzerrabar.presentation.RestController;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@WebMvcTest(GeneralRestController.class)
@AutoConfigureRestDocs
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class GeneralRestControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void return_hello_message_when_requested() throws Exception {
        mvc.perform(get("") // Adjust the URL if needed
                        .accept(MediaType.TEXT_HTML)) // Expecting HTML content
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)) // Allows for charset
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Hello! Welcome to the LibraryApp API"))) // Checking for part of the hello message
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<ul>"))) // Ensuring that the HTML <ul> tag (list of instructions) is present
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<a href='http://localhost:8080/swagger-ui/index.html'"))) // Verifying that the Swagger UI link is present
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<a href='http://localhost:8080/docs/index.html'"))); // Verifying that the API Docs link is present
    }
}