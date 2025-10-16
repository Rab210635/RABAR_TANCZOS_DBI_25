package spengergasse.at.sj2425scherzerrabar.presentation.www;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import spengergasse.at.sj2425scherzerrabar.service.BuyableBookService;

@Controller
@RequestMapping("/www/buyableBooks")
public class BuyableBookController {
    private final BuyableBookService buyableBookService;

    public BuyableBookController(BuyableBookService buyableBookService) {
        this.buyableBookService = buyableBookService;
    }

    @GetMapping
    public String getAllBuyableBooks(Model model) {
        model.addAttribute("buyableBooks",buyableBookService.getAllBuyableBooks());
        return "buyableBooks/index";
    }
}
