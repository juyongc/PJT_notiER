package com.notier.viewController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/currency-view")
    public String currencyView() {

        return "currency-view";
    }

    @GetMapping("/home")
    public String home() {

        return "home";
    }

}
