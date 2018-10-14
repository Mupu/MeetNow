package me.mupu.server.controller.user;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Secured("ROLE_USER")
@RequestMapping("/user")
public class UserController {

    @RequestMapping
    public ModelAndView userHome() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("user/userHome");
        return mv;
    }

}
