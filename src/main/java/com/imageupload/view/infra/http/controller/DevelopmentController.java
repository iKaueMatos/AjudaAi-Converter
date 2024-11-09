package com.imageupload.view.infra.http.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DevelopmentController {
    @GetMapping("/development")
    public ModelAndView showRedimensioningPage() {
        ModelAndView modelAndView = new ModelAndView("pages/development/development");
        return modelAndView;
    }
}