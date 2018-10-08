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
@RolesAllowed({"USER", "ADMIN"})
//@Secured({"ROLE_USER", "ROLE_ADMIN"})
public class TestController {
    

    @Autowired
    AusstattungsgegenstandRepository repo;

    @RequestMapping("/test")
    public ModelAndView test(@RequestParam(required = false, name = "name") String name,
                              @RequestParam(required = false, name = "anzahl") Integer anzahl) {

//        repo.saveGegenstand(name, anzahl);

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

    @RequestMapping("/loggedout")
    public ModelAndView loggedout() {

        ModelAndView mv = new ModelAndView();
        mv.setViewName("loggedout");

        return mv;
    }

}
