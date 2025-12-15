package com.sbs.tutorial.app1.domain.clean.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AlertController {

    @GetMapping("/alert")
    public String alert(String msg, String next, Model model) {
        model.addAttribute("msg",msg);
        model.addAttribute("next",next);
        return "alert";
    }


}
