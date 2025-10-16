package spengergasse.at.sj2425scherzerrabar.presentation.www;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import spengergasse.at.sj2425scherzerrabar.service.LibraryService;
import spengergasse.at.sj2425scherzerrabar.service.OrderService;

@Controller
@RequestMapping("/www/libraries")
public class LibraryController {
    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping
    public String getAllLibraries(Model model) {
        model.addAttribute("libraries",libraryService.getLibraries());
        return "libraries/index";
    }

}
