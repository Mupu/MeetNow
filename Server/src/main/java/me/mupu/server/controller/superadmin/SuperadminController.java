package me.mupu.server.controller.superadmin;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Secured("ROLE_SUPERADMIN")
@RequestMapping("/superadmin")
public class SuperadminController {

    @RequestMapping
    public ModelAndView superadminHome() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("superadmin/superadminHome");
        return mv;
    }

}
