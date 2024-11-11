package com.novatolls.view.infra.http.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ImageRedmensioningPageController {
    @GetMapping("/redmensioning")
    public ModelAndView showRedimensioningPage() {
        ModelAndView modelAndView = new ModelAndView("pages/redmensioning-tool/redmensioning");
        return modelAndView;
    }
}
