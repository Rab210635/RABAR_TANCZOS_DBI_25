package spengergasse.at.sj2425scherzerrabar.presentation.www.authors;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import spengergasse.at.sj2425scherzerrabar.commands.AuthorCommand;
import spengergasse.at.sj2425scherzerrabar.presentation.RestController.LoggingController;
import spengergasse.at.sj2425scherzerrabar.presentation.www.RedirectForwardSupport;
import spengergasse.at.sj2425scherzerrabar.service.AuthorService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/www/authors")
public class AuthorController implements RedirectForwardSupport {

    private final AuthorService authorService;
    private Logger logger = LoggerFactory.getLogger(LoggingController.class);

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public String getAllAuthors(Model model,
                                @RequestParam(value = "error", required = false) String error) {
        logger.debug("entered Web AuthorController getAllAuthors");
        model.addAttribute("authors", authorService.getAuthors());
        model.addAttribute("error", error);
        return "authors/index";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        logger.debug("entered Web AuthorController showAddForm");
        CreateAuthorForm form = new CreateAuthorForm();
        form.getStreetAndNumber().add("");
        form.getCity().add("");
        form.getPlz().add("");
        model.addAttribute("form", form);
        return "authors/create";
    }


    @GetMapping("/edit/{apiKey}")
    public String showEditForm(@PathVariable String apiKey, Model model) {
        logger.debug("entered Web AuthorController showEditForm");
        var author = authorService.getAuthor(apiKey);
        CreateAuthorForm form = new CreateAuthorForm();
        form.setPenname(author.penname());
        form.setFirstname(author.firstname());
        form.setLastname(author.lastname());
        form.setEmailAddress(author.emailAddress());

        List<String> streetAndNumber = new ArrayList<>();
        List<String> city = new ArrayList<>();
        List<String> plz = new ArrayList<>();

        for (String address : author.address()) {
            String[] parts = address.split("-", 3);
            streetAndNumber.add(parts.length > 0 ? parts[0] : "");
            city.add(parts.length > 1 ? parts[1] : "");
            plz.add(parts.length > 2 ? parts[2] : "");
        }

        form.setStreetAndNumber(streetAndNumber);
        form.setCity(city);
        form.setPlz(plz);

        model.addAttribute("form", form);
        model.addAttribute("apiKey", apiKey);
        return "authors/edit";
    }

    @PostMapping("/add")
    public String handleAddForm(@Valid @ModelAttribute("form") CreateAuthorForm form,
                                BindingResult result,
                                Model model) {
        logger.debug("entered Web AuthorController handleAddForm");
        validateAddressFields(form, result);

        validatePlzFields(form, result);

        if (result.hasErrors()) {
            return "authors/create";
        }

        authorService.createAuthor(form.getAuthorCommand());
        return redirectTo("/www/authors");
    }

    @PostMapping("/edit/{apiKey}")
    public String handleEditForm(@PathVariable String apiKey,
                                 @Valid @ModelAttribute("form") CreateAuthorForm form,
                                 BindingResult result,
                                 Model model) {
        logger.debug("entered Web AuthorController handleEditForm");
        validateAddressFields(form, result);

        validatePlzFields(form, result);

        if (result.hasErrors()) {
            model.addAttribute("apiKey", apiKey);
            return "authors/edit";
        }

        AuthorCommand command = form.getAuthorCommand(apiKey);
        authorService.updateAuthor(command);
        return redirectTo("/www/authors");
    }

    private void validatePlzFields(CreateAuthorForm form, BindingResult result) {
        logger.debug("entered Web AuthorController validatePlzFields");
        for (int i = 0; i < form.getPlz().size(); i++) {
            String plz = form.getPlz().get(i);
            if (!plz.matches("\\d+")) {
                result.rejectValue("plz[" + i + "]", "error.plz", "PLZ muss eine Zahl sein");
            }
        }
    }


    private void validateAddressFields(CreateAuthorForm form, BindingResult result) {
        logger.debug("entered Web AuthorController validateAddressFields");
        for (int i = 0; i < form.getStreetAndNumber().size(); i++) {
            String street = form.getStreetAndNumber().get(i);
            String city = form.getCity().get(i);
            String plz = form.getPlz().get(i);

            if (street == null || street.trim().isEmpty()) {
                result.rejectValue("streetAndNumber[" + i + "]", "NotBlank", "Straße darf nicht leer sein");
            }
            if (city == null || city.trim().isEmpty()) {
                result.rejectValue("city[" + i + "]", "NotBlank", "Stadt darf nicht leer sein");
            }
            if (plz == null || plz.trim().isEmpty()) {
                result.rejectValue("plz[" + i + "]", "NotBlank", "PLZ darf nicht leer sein");
            }
            if (plz != null && !plz.trim().matches("\\d+")) {
                result.rejectValue("plz[" + i + "]", "Pattern", "PLZ muss nur aus Zahlen bestehen");
            }
        }
    }



    @PostMapping("/delete")
    public String deleteAuthor(@RequestParam String apiKey, Model model) {
        logger.debug("entered Web AuthorController delete");

        try {
            authorService.deleteAuthor(apiKey);
        } catch (Exception e) {
            System.out.println("Deleting author with apiKey: " + apiKey +" Error" + e.getMessage());
            return getAllAuthors(model,"The Author can not be deleted, they might still be remarked in a Book.");
        }

        model.addAttribute("authors", authorService.getAuthors());
        return redirectTo("/www/authors");
    }


    @GetMapping("/show/{apiKey}")
    public String showAuthor(@PathVariable String apiKey, Model model) {
        logger.debug("entered Web AuthorController showAuthor");
        model.addAttribute("author", authorService.getAuthor(apiKey));
        return "authors/show";
    }
}
