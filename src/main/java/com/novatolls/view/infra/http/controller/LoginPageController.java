package com.novatolls.view.infra.http.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginPageController {
    @GetMapping("/login")
    public ModelAndView handle() {
        ModelAndView modelAndView = new ModelAndView("pages/auth/login/login");
        return modelAndView;
    }
}
