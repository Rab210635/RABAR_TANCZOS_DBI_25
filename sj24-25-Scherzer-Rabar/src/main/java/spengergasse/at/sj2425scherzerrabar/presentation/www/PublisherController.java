package spengergasse.at.sj2425scherzerrabar.presentation.www;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import spengergasse.at.sj2425scherzerrabar.service.BookService;
import spengergasse.at.sj2425scherzerrabar.service.PublisherService;

@Controller
@RequestMapping("/www/publishers")
public class PublisherController {
    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @GetMapping
    public String getAllPublishers(Model model) {
        model.addAttribute("publishers",publisherService.getAllPublishers());
        return "publishers/index";
    }

}
