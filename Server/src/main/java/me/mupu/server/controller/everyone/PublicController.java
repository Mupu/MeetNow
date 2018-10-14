package me.mupu.server.controller.everyone;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * EDIT ACCESS RIGHTS IN SecurityConfiguration
 */
@Controller
public class PublicController {

    @RequestMapping("/login")
    public ModelAndView login() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("everyone/login");

        return mv;
    }

    @RequestMapping(value = {"/", "/home"})
    public ModelAndView home() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("everyone/home");

        return mv;
    }

}
