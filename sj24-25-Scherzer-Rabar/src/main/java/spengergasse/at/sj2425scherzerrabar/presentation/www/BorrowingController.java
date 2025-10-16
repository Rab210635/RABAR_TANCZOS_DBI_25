package spengergasse.at.sj2425scherzerrabar.presentation.www;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import spengergasse.at.sj2425scherzerrabar.service.BookService;
import spengergasse.at.sj2425scherzerrabar.service.BorrowingService;


@Controller
@RequestMapping("/www/borrowings")
public class BorrowingController {
    private final BorrowingService borrowingService;

    public BorrowingController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    @GetMapping
    public String getAllBorrowings(Model model) {
        model.addAttribute("borrowings",borrowingService.getAllBorrowings());
        return "borrowings/index";
    }

}
