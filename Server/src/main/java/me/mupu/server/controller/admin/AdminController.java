package me.mupu.server.controller.admin;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Secured("ROLE_ADMIN")
@RequestMapping("/admin")
public class AdminController {

    @RequestMapping
    public ModelAndView adminHome() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("admin/adminHome");
        return mv;
    }

}
