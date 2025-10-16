package spengergasse.at.sj2425scherzerrabar.presentation.www;

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

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/www")
public class HomeController implements RedirectForwardSupport {

    private Logger logger = LoggerFactory.getLogger(LoggingController.class);

    public HomeController() {
    }

    @GetMapping
    public String getAllAuthors(Model model,
                                @RequestParam(value = "error", required = false) String error,
                                Principal principal
    ) {
        logger.debug("entered Web AuthorController getAllAuthors");
        model.addAttribute("principal", principal.getName());
        model.addAttribute("error", error);
        return "home";
    }

}
