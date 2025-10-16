package spengergasse.at.sj2425scherzerrabar.presentation.www;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import spengergasse.at.sj2425scherzerrabar.service.CopyService;

@Controller
@RequestMapping("/www/copies")
public class CopyController {
    private final CopyService copyService;

    public CopyController(CopyService copyService) {
        this.copyService = copyService;
    }

    @GetMapping
    public String getAllCopies(Model model) {
        model.addAttribute("copies",copyService.getCopies());
        return "copies/index";
    }
}
