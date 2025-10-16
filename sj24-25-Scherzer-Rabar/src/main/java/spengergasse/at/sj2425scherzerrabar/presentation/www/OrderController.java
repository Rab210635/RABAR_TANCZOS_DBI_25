package spengergasse.at.sj2425scherzerrabar.presentation.www;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import spengergasse.at.sj2425scherzerrabar.service.OrderService;
import spengergasse.at.sj2425scherzerrabar.service.PublisherService;

@Controller
@RequestMapping("/www/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String getAllOrders(Model model) {
        model.addAttribute("orders",orderService.getAllOrders());
        return "orders/index";
    }

}
