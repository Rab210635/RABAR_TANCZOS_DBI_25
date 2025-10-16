package spengergasse.at.sj2425scherzerrabar.presentation.www;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import spengergasse.at.sj2425scherzerrabar.service.CustomerService;
import spengergasse.at.sj2425scherzerrabar.service.LibraryService;
import spengergasse.at.sj2425scherzerrabar.service.OrderService;

@Controller
@RequestMapping("/www/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public String getAllCustomers(Model model) {
        model.addAttribute("customers",customerService.getCustomers());
        return "customers/index";
    }

}
