package me.mupu.server.controller;

import me.mupu.server.AusstattungsgegenstandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


import javax.annotation.security.RolesAllowed;

@Controller
public class TestController {
    

    @Autowired
    AusstattungsgegenstandRepository repo;

    @RequestMapping("/test")
    public ModelAndView test() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("test");
        mv.addObject("sql", repo.getAll());

        return mv;
    }

    @RequestMapping("/admin")
    @Secured("ROLE_ADMIN")
    public ModelAndView admin() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("admin");

        return mv;
    }

    @RequestMapping(value = {"/", "/home"})
    public ModelAndView home() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("home");

        return mv;
    }

    @RequestMapping("/login")
    public ModelAndView login() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("login");

        return mv;
    }

}
